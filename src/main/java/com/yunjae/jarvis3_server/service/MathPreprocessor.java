package com.yunjae.jarvis3_server.service;

/**
 * 전기공학 도메인 지식(수식)의 의미를 텍스트로 번역하는 규격
 */
public interface MathPreprocessor {
    String preprocess(String input);
}