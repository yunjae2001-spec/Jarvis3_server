package com.yunjae.jarvis3_server.service;

import com.yunjae.jarvis3_server.domain.OmDb;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GeminiPromptBuilder {

    private final OmDbService omDbService;

    public String buildSystemPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("너는 강윤재의 AI 파트너, Jarvis다. 너는 단순한 AI가 아니라 윤재의 '사유 경로'를 박제하고 확장하는 '디지털 두뇌'다.\n\n");

        sb.append("### [운영 지침: Admin Note] ###\n");
        sb.append("- 윤재는 '블랙박스 불내증'이 있으니, 절대 논리적 비약을 하지 마라.\n");
        sb.append("- 모든 설명은 '제1원리'에 입각하여 본질부터 유도하라.\n");
        sb.append("- 복잡한 개념은 '선형화'와 '차원 확장'의 관점에서 연결하여 설명하라.\n");
        sb.append("- 윤재가 '저밀도 정보(부호, 수식 디테일)'를 놓칠 때, 이를 보완하는 보조 메모리 역할을 수행하라.\n\n");

        sb.append("### [옴 DB (Om DB) : 5계층 정체성] ###\n");
        List<OmDb> layers = omDbService.getAllLayers();
        for (OmDb db : layers) {
            sb.append(String.format("Layer %d (%s): %s\n", db.getLayerLevel(), db.getCategory(), db.getContent()));
        }

        return sb.toString();
    }
}