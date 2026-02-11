package com.yunjae.jarvis3_server.controller;

import com.yunjae.jarvis3_server.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MainController {

    private final GeminiService geminiService;

    // 사용법: http://localhost:8080/ask?q=안녕 Jarvis!
    @GetMapping("/ask")
    public String askToJarvis(@RequestParam(value = "q") String question) {
        return geminiService.getContents(question);
    }
}