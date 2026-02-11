package com.yunjae.jarvis3_server.service;

import com.yunjae.jarvis3_server.config.GlobalConfig;
import com.yunjae.jarvis3_server.dto.GeminiResponse; // DTO 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final GlobalConfig config;
    private final RestClient restClient = RestClient.create();

    public String getContents(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + config.getGeminiKey();

        String requestBody = "{ \"contents\": [{ \"parts\":[{ \"text\": \"" + prompt + "\" }] }] }";

        try {
            // 1. JSON을 GeminiResponse 객체로 바로 변환해서 받아옴
            GeminiResponse response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(GeminiResponse.class);

            // 2. 복잡한 구조를 파고 들어가서 '텍스트'만 추출
            if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                return response.getCandidates().get(0).getContent().getParts().get(0).getText();
            }
            return "응답 데이터를 찾을 수 없습니다.";

        } catch (Exception e) {
            return "AI 통신 중 오류 발생: " + e.getMessage();
        }
    }
}