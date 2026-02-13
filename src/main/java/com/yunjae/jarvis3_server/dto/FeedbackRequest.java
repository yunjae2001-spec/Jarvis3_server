package com.yunjae.jarvis3_server.dto;

import lombok.Data;

@Data
public class FeedbackRequest {
    private Long vectorNodeId;
    private FeedbackType feedbackType;
}