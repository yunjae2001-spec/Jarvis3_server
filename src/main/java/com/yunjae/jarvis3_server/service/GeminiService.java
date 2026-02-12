package com.yunjae.jarvis3_server.service;

import com.yunjae.jarvis3_server.config.GlobalConfig;
import com.yunjae.jarvis3_server.dto.GeminiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final GlobalConfig config;
    private final GeminiPromptBuilder promptBuilder;
    private final RestClient restClient = RestClient.builder().build();
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 해체기

    // 1. [기존 기능] 대화 생성 (Chat)
    public String getContents(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + config.getGeminiKey();

        String systemInstruction = promptBuilder.buildSystemPrompt();

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
            log.error("AI Generation Error", e);
            return "AI 통신 중 오류 발생: " + e.getMessage();
        }
    }

    // ==========================================================
    // 2. [신규 기능] 텍스트 임베딩 (Vectorization)
    // ==========================================================
    public List<Double> getEmbedding(String text) {
        // [확인된 정답] v1beta와 gemini-embedding-001 조합
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-embedding-001:embedContent?key=" + config.getGeminiKey();

        Map<String, Object> requestBody = Map.of(
                "model", "models/gemini-embedding-001", // 로그에서 확인한 정확한 이름
                "content", Map.of(
                        "parts", List.of(Map.of("text", text))
                )
        );

        try {
            EmbeddingResponse response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(EmbeddingResponse.class);

            if (response != null && response.embedding != null) {
                return response.embedding.values;
            }
            throw new RuntimeException("API 응답에 벡터 데이터가 없습니다.");
        } catch (Exception e) {
            log.error("Embedding API Error: {}", e.getMessage());
            throw new RuntimeException("임베딩 변환 실패: " + e.getMessage());
        }
    }

    // 사용 가능한 모델 리스트를 출력하는 디버깅 메서드
    public void listAvailableModels() {
        String url = "https://generativelanguage.googleapis.com/v1beta/models?key=" + config.getGeminiKey();

        try {
            String response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);
            log.info("Available Gemini Models: \n{}", response);
        } catch (Exception e) {
            log.error("Failed to list models: {}", e.getMessage());
        }
    }


    public String generateResponse(String prompt) {
        // 2026 표준 v1 엔드포인트 및 gemini-2.0-flash 모델 사용
        String url = "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key=" + config.getGeminiKey();

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        try {
            String rawJson = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            // [핵심] JSON 트리 구조를 탐색해서 'text'만 쏙 빼오기
            JsonNode root = objectMapper.readTree(rawJson);
            return root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (Exception e) {
            log.error("JSON Parsing Error: {}", e.getMessage());
            return "답변을 정제하는 데 실패했습니다.";
        }
    }

    // --- [Inner DTO] 임베딩 응답용 레코드 (외부 파일 안 만들고 여기서 처리) ---
    private record EmbeddingResponse(Embedding embedding) {
        record Embedding(List<Double> values) {}
    }
}