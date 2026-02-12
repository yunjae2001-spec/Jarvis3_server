package com.yunjae.jarvis3_server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultService {

    private final InsightService insightService;
    private final GeminiService geminiService;

    public String askJarvis(String userQuery) {
        // 1. [검색] 질문과 관련된 과거의 깨달음 3개를 가져옵니다.
        List<String> relatedInsights = insightService.recallInsight(userQuery);

        // 2. [증강] 검색된 기억들을 하나의 맥락(Context)으로 합칩니다.
        String context = String.join("\n- ", relatedInsights);

        // 3. [프롬프트 구성] Jarvis의 페르소나와 지식을 주입합니다.
        String finalPrompt = String.format("""
                당신의 이름은 'Jarvis'이며, 사용자 '윤재 수석'의 개인 AI 비서입니다.
                다음은 윤재 수석이 과거에 기록한 깨달음(Insights)들입니다:
                
                [과거 기억]
                - %s
                
                위 기억들을 바탕으로 윤재 수석의 질문에 대해 깊이 있고 공학적인 통찰을 담아 답변해 주세요. 
                과거 기록에 없는 내용이라도 수석님의 가치관을 존중하며 답변하세요.
                
                질문: %s
                """, context, userQuery);

        // 4. [생성] Gemini에게 최종 답변을 요청합니다.
        return geminiService.generateResponse(finalPrompt);
    }
}