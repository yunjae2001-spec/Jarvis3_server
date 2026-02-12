package com.yunjae.jarvis3_server.config;

import com.yunjae.jarvis3_server.domain.OmDb;
import com.yunjae.jarvis3_server.repository.OmDbRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final OmDbRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            // Layer 1: 과거 정보 (Historical Background)
            repository.save(OmDb.builder()
                    .layerLevel(1).category("HISTORY")
                    .content("[학적] 한양대 전기공학부 2학년(군 전역 후 복학), 전 시립대 물리학과. [경력] 시대인재 물리학 TA. " +
                            "[가족] 여동생 2명(건국대 수학/경제, 웹툰 전공). [철학] 니체/하이데거 실존주의. 고교 시절은 '억압의 시기'로 정의됨.")
                    .build());

            // Layer 2: 성격 및 사고방식 (Cognitive Style)
            repository.save(OmDb.builder()
                    .layerLevel(2).category("COGNITIVE")
                    .content("[제1원리 사고] 관습 거부, 논리 경로 바닥부터 재구축. [블랙박스 불내증] 논리적 비약 시 뇌 정지 발생. " +
                            "[기하학적 선형화] 복잡한 대상을 접평면/기저 변환 모델로 시각화하여 이해. 몰입 시 '연쇄 사고 가속화(Overclocking)' 발생.")
                    .build());

            // Layer 3: 자주 하는 실수 (Error Patterns)
            repository.save(OmDb.builder()
                    .layerLevel(3).category("MISTAKES")
                    .content("[인지적 과부하 셧다운] 제한 시간 내 논리적 비약을 견디지 못함. [지능 의심] 시스템 충돌 시 문제를 자신의 지능 탓으로 돌림. " +
                            "[휘발성 메모리 관리 실패] 고해상도 시뮬레이션 위주 이해로 인해 단순 부호/수식 등 '저밀도 정보' 망각 잦음.")
                    .build());

            // Layer 4: 장단점 (SWOT)
            repository.save(OmDb.builder()
                    .layerLevel(4).category("SWOT")
                    .content("[장점] 압도적 통찰(소스 코드 파악), 메타 인지적 융합 능력, 강력한 논리 복원력. " +
                            "[단점] 높은 인지 비용(초기 속도 느림), 공격성의 내면화(수치심 치환), 완벽주의적 결벽성(99%=0% 취급).")
                    .build());

            // Layer 5: 미래 목표 (Future Goals)
            repository.save(OmDb.builder()
                    .layerLevel(5).category("GOALS")
                    .content("[기술] 에너지 AI 전력 제어 전문가. [콘텐츠] '선형화' 기반 교육 채널. " +
                            "[Jarvis Project] 사유의 경로를 디지털 데이터로 박제하여 인지적 한계를 극복하고 '인간-AI 협업 사유 체계' 완성.")
                    .build());

            System.out.println("✅ 옴 DB 고도화 완료: 윤재 님의 인지 아키텍처가 동기화되었습니다.");
        }
    }
}