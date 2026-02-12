package com.yunjae.jarvis3_server.service;

import com.yunjae.jarvis3_server.domain.RawArchive;
import com.yunjae.jarvis3_server.domain.VectorNode;
import com.yunjae.jarvis3_server.repository.RawArchiveRepository;
import com.yunjae.jarvis3_server.repository.VectorNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsightService {

    private final RawArchiveRepository rawArchiveRepository;
    private final VectorNodeRepository vectorNodeRepository;
    private final GeminiService geminiService;

    /**
     * [저장] 깨달음을 정제하여 원본과 벡터로 이원화 저장
     */
    @Transactional
    public void digestInsight(String topic, String content) {
        // 1. 원본 저장 (Raw Archive)
        RawArchive savedArchive = rawArchiveRepository.save(new RawArchive(topic, content));
        log.info("Insight Raw Archived: ID={}", savedArchive.getId());

        // 2. 벡터화 (Embedding)
        List<Double> embeddingVector = geminiService.getEmbedding(content);

        // 3. [중요] 리스트를 문자열 포맷으로 변환 ("[0.1,0.2,...]")
        String vectorString = embeddingVector.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));

        // 4. 시냅스 연결 (Vector Node) - 이제 String을 전달합니다.
        vectorNodeRepository.save(new VectorNode(savedArchive, vectorString));
        log.info("Insight Vectorized & Connected.");
    }

    /**
     * [회상] 모호한 질문으로 과거의 깨달음을 검색
     */
    @Transactional(readOnly = true)
    public List<String> recallInsight(String vagueQuery) {
        // 1. 질문을 벡터로 변환
        List<Double> queryVector = geminiService.getEmbedding(vagueQuery);

        // 2. [중요] 검색용 벡터도 동일한 문자열 포맷으로 변환
        String vectorString = queryVector.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));

        log.info("Searching with query vector sample: {}...", vectorString.substring(0, 50));

        // 3. 유사도 검색 실행 (상위 3개)
        List<VectorNode> nearestNodes = vectorNodeRepository.findNearestNeighbors(vectorString, 3);

        // 4. 원본 텍스트만 추출하여 반환
        return nearestNodes.stream()
                .map(node -> node.getArchive().getContent())
                .collect(Collectors.toList());
    }
}