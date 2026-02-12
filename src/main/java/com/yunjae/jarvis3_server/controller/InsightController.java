package com.yunjae.jarvis3_server.controller;

import com.yunjae.jarvis3_server.service.InsightService;
import com.yunjae.jarvis3_server.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insights")
@RequiredArgsConstructor
public class InsightController {

    private final InsightService insightService;
    private final GeminiService geminiService;

    // 1. 깨달음 저장 (Digest)
    @PostMapping("/digest")
    public String digest(@RequestBody InsightRequest request) {
        insightService.digestInsight(request.topic(), request.content());
        return "Insight successfully distilled and archived.";
    }

    // 2. 깨달음 회상 (Recall)
    @GetMapping("/recall")
    public List<String> recall(@RequestParam String query) {
        return insightService.recallInsight(query);
    }

    // DTO
    public record InsightRequest(String topic, String content) {}
}