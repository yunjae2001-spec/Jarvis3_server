package com.yunjae.jarvis3_server.controller;

import com.yunjae.jarvis3_server.dto.FeedbackRequest;
import com.yunjae.jarvis3_server.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Double> adjustWeight(@RequestBody FeedbackRequest request) {
        Double updatedStrength = feedbackService.updateSynapseStrength(request);
        return ResponseEntity.ok(updatedStrength);
    }
}