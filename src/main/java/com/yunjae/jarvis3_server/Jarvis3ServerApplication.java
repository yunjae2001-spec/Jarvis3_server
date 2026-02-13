package com.yunjae.jarvis3_server;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // [추가] 스케줄링 기능을 위한 임포트

@SpringBootApplication
@EnableScheduling // [핵심 추가] 이 어노테이션이 있어야 @Scheduled 로직이 작동합니다.
public class Jarvis3ServerApplication {

    public static void main(String[] args) {
        // 1. .env 파일을 명시적으로 로드해서 시스템 속성에 주입
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });
            System.out.println("✅ .env 파일 로드 성공!");
        } catch (Exception e) {
            System.out.println("⚠️ .env 로드 중 문제 발생: " + e.getMessage());
        }

        // 2. 스프링 부트 가동
        SpringApplication.run(Jarvis3ServerApplication.class, args);
    }
}