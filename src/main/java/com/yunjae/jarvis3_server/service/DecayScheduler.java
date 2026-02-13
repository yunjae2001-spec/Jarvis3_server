package com.yunjae.jarvis3_server.service;

import com.yunjae.jarvis3_server.config.DecayConstants;
import com.yunjae.jarvis3_server.repository.VectorNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DecayScheduler {

    private final VectorNodeRepository vectorNodeRepository;

    /**
     * 매일 자정(00:00:00) 시스템 엔트로피 제어를 수행합니다.
     */
    @Scheduled(cron = "0 0 0 * * *")// [테스트용] 10초 주기 유지
    @Transactional
    public void performSystemMemoryCleanup() {
        LocalDateTime limitDate = LocalDateTime.now().minusDays(DecayConstants.PROTECTION_DAYS);
        // [보호 임계값] 보통 2.0으로 설정하여 수석님이 각인시킨 지식을 보호합니다.
        double threshold = DecayConstants.PROTECTION_THRESHOLD;

        log.info("[Temporal Decay] 망각 프로세스 시작. 기준 활성일: {}", limitDate);

        // 1. 일상 세션 (SESSION): 보호막 없이 빠르게 감쇠 (999.0은 모든 데이터를 깎겠다는 의미)
        vectorNodeRepository.applyDecayWithProtection("SESSION", DecayConstants.SESSION_RATE, limitDate, 999.0);

        // 2. 전문 지식 레이어: 2.0 이상은 절대 보호 (금강석 지식화)
        // 수석님이 사용하는 모든 토픽을 여기에 나열하세요. 누락을 방지하는 안전장치입니다.
        String[] professionalTopics = {"INSIGHT", "ENERGY", "PHYSICS", "PHILOSOPHY"};

        for (String topic : professionalTopics) {
            vectorNodeRepository.applyDecayWithProtection(topic, DecayConstants.INSIGHT_RATE, limitDate, threshold);
        }

        // 3. 약해진 시냅스 소거 (하한선 0.1)
        vectorNodeRepository.purgeWeakSynapses(DecayConstants.MIN_FLOOR);

        log.info("[Temporal Decay] 시스템 정돈이 완료되었습니다.");
    }
}