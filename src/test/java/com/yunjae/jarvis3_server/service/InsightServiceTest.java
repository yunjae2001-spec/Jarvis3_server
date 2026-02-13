package com.yunjae.jarvis3_server.service;

import io.github.cdimascio.dotenv.Dotenv; // Dotenv 임포트 필요
import org.junit.jupiter.api.BeforeAll;
import com.yunjae.jarvis3_server.domain.RawArchive;
import com.yunjae.jarvis3_server.repository.RawArchiveRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// AssertJ의 static 메서드 (이게 있어야 assertThat 에러가 안 납니다)
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class InsightServiceTest {
    @BeforeAll
    static void setup() {
        Dotenv dotenv = Dotenv.configure().load();
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
    }

    @Autowired
    private InsightService insightService;

    @Autowired
    private RawArchiveRepository rawArchiveRepository;

    @Test
    @DisplayName("전기공학 수식 해석 및 검색 통합 테스트")
    void integrateBmsFormulaTest() {
        // Given: 한양대 전기공학 프로젝트 'Jarvis'의 핵심 SoC 수식 준비
        String topic = "BMS Test";
        String content = "배터리 충전 상태 공식: $SoC_{t}$";

        // When: 지식 주입 (DescriptiveMathPreprocessor 작동)
        insightService.digestInsight(topic, content);

        // Then: 데이터 무결성 확인
        List<RawArchive> archives = rawArchiveRepository.findAll();
        assertThat(archives).isNotEmpty();

        // Recall: '충전 상태'라는 자연어로 수식($SoC$)이 검색되는지 확인
        List<String> memories = insightService.recallInsight("충전 상태");
        assertThat(memories).anyMatch(m -> m.contains("SoC_{t}"));
    }
}