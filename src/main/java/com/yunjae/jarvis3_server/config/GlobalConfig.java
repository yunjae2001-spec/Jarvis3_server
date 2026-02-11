package com.yunjae.jarvis3_server.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration // 이 클래스가 스프링의 설정 전담반임을 선언
@Getter        // getGeminiKey() 메서드를 자동으로 생성
public class GlobalConfig {

    // @Value는 .env 파일의 변수명을 찾아 값을 주입해 줌
    @Value("${GEMINI_API_KEY}")
    private String geminiKey;

    @Value("${DB_PASSWORD}")
    private String dbPassword;
}