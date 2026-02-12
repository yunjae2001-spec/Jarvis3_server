package com.yunjae.jarvis3_server.config;

import com.yunjae.jarvis3_server.domain.OmConstitution;
import com.yunjae.jarvis3_server.repository.OmConstitutionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(OmConstitutionRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(OmConstitution.builder()
                        .layerLevel(1)
                        .category("Identity")
                        .content("헌법 1조: 나는 2001년생 12월 18일에 태어났다.")
                        .isLocked(true)
                        .build());
                System.out.println("✅ [Initialization] Jarvis 헌법 제1조가 선포되었습니다.");
            }
        };
    }
}