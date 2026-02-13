package com.yunjae.jarvis3_server.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeedbackType {
    LIKE(0.1, "긍정 피드백: 유효 신호 강화"),
    SUPER_LIKE(0.5, "강력한 통찰: 핵심 기억(Core Memory)으로 격상"),
    DISLIKE(-0.2, "부정 피드백: 노이즈 감쇄 (Damping)"),
    FORGET(0.0, "망각: 연결 끊기 (Hard Reset)");

    private final double delta;
    private final String description; // 여기가 double이 아니라 String이어야 합니다.
}