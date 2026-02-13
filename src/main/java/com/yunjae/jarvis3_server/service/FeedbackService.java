package com.yunjae.jarvis3_server.service;

import com.yunjae.jarvis3_server.domain.VectorNode;
import com.yunjae.jarvis3_server.dto.FeedbackRequest;
import com.yunjae.jarvis3_server.dto.FeedbackType;
import com.yunjae.jarvis3_server.repository.VectorNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final VectorNodeRepository vectorNodeRepository;

    // 제어 한계 설정 (Saturation Limits) - 시스템 안정성을 위한 Hard Clamp
    private static final double MAX_CAP = 5.0;
    private static final double MIN_FLOOR = 0.1;

    @Transactional
    public Double updateSynapseStrength(FeedbackRequest request) {
        // 1. 타겟 노드 식별 (Plant Identification)
        VectorNode node = vectorNodeRepository.findById(request.getVectorNodeId())
                .orElseThrow(() -> new IllegalArgumentException("Target Node not found: " + request.getVectorNodeId()));

        FeedbackType type = request.getFeedbackType();
        double currentStrength = node.getSynapseStrength();

        // 2. 특이점 처리 (Singularity Handling): FORGET
        if (type == FeedbackType.FORGET) {
            node.setSynapseStrength(0.0);
            // VectorNode 엔티티에 isDeleted 필드가 있다면 true로 설정 필요
            // node.setDeleted(true);
            log.info("[Feedback Loop] Connection Severed (FORGET): Node ID {}", node.getId());
            return 0.0;
        }

        /**
         * [Control Theory Note - by Yunjae]
         * 현재 제어 모델: Linear Proportional Adjustment (선형 비례 제어)
         * 수식: u(t) = u(t-1) + K (Gain)
         * * [Engineering Comment]
         * 1. 선형성(Linearity)의 한계:
         * 현재 가중치가 1.0일 때의 +0.1과, 4.0일 때의 +0.1은 인간이 느끼는 '효용의 크기'가 다릅니다.
         * 인간의 감각은 베버-페히너 법칙(Weber-Fechner law)에 따라 로그 스케일로 반응하므로,
         * 추후 가중치가 높아질수록 변화율(Gain)이 줄어드는 Logarithmic Damping 도입을 고려해야 합니다.
         * * 2. 현재 구현:
         * 초기 시스템 복잡도를 낮추기 위해 선형 모델 + Clamping(Saturation)으로 Windup 현상을 방지합니다.
         */

        // 3. 가중치 연산 (Calculation)
        double calculatedStrength = currentStrength + type.getDelta();

        // 4. 클램핑 (Clamping / Saturation)
        // 가중치가 무한히 발산하거나 0 이하로 소멸하는 것을 방지
        double newStrength = Math.min(MAX_CAP, Math.max(MIN_FLOOR, calculatedStrength));

        // 5. 상태 업데이트 (State Update)
        node.setSynapseStrength(newStrength);

        log.info("[Feedback Loop] Synapse Adjusted: Node {} | {:.2f} -> {:.2f} (Input: {})",
                node.getId(), currentStrength, newStrength, type);

        return newStrength;
    }
}