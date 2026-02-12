package com.yunjae.jarvis3_server.service;

import com.yunjae.jarvis3_server.config.GlobalConfig;
import com.yunjae.jarvis3_server.dto.GeminiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final GlobalConfig config;
    private final GeminiPromptBuilder promptBuilder; // PromptBuilder로 통합 관리
    private final RestClient restClient = RestClient.create();

    public String getContents(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + config.getGeminiKey();

        // 1. 옴 DB의 5개 레이어가 모두 포함된 시스템 프롬프트 생성
        String systemInstruction = promptBuilder.buildSystemPrompt();

        // 2. API 규격에 맞춘 Payload 구성
        Map<String, Object> requestBody = Map.of(
                "system_instruction", Map.of(
                        "parts", List.of(Map.of("text", systemInstruction))
                ),
                "contents", List.of(
                        Map.of("role", "user", "parts", List.of(Map.of("text", prompt)))
                )
        );

        try {
            GeminiResponse response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(GeminiResponse.class);

            if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                return response.getCandidates().get(0).getContent().getParts().get(0).getText();
            }
            return "응답 데이터를 찾을 수 없습니다.";
        } catch (Exception e) {
            return "AI 통신 중 오류 발생: " + e.getMessage();
        }
    }
}