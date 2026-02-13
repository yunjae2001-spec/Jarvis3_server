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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsightService {

    private final RawArchiveRepository rawArchiveRepository;
    private final VectorNodeRepository vectorNodeRepository;
    private final GeminiService geminiService;
    private final MathPreprocessor mathPreprocessor; // [신규] 수식 해석기 주입

    /**
     * [저장] 수식을 해석하여 고밀도 설명적 임베딩 생성 및 저장
     */
    @Transactional
    public void digestInsight(String topic, String content) {
        // 1. [기능 추가] 동일한 내용(content)이 이미 있는지 DB에서 먼저 확인합니다.
        Optional<RawArchive> existingArchive = rawArchiveRepository.findByContent(content);

        if (existingArchive.isPresent()) {
            // [중복 시나리오] 이미 알고 있는 내용이므로 시냅스만 강화합니다.
            VectorNode node = vectorNodeRepository.findByArchive(existingArchive.get())
                    .orElseThrow(() -> new IllegalStateException("Consistency Error: Node not found for archive"));

            node.updateSynapse(true); // 기존 VectorNode에 정의된 +0.1 강화 로직 실행
            log.info("[Auto-Reinforcement] 동일한 통찰이 감지되었습니다. ID: {} 시냅스 강도를 {}로 강화합니다.",
                    node.getId(), node.getSynapseStrength());
            // 중복인 경우 여기서 메서드를 종료하여 비용이 발생하는 임베딩 API 호출을 생략합니다.
            return;
        }

        // [신규 시나리오] 처음 보는 내용일 경우 기존 로직을 그대로 수행합니다.
        // 1. 원본 저장 (Single Source of Truth)
        RawArchive savedArchive = rawArchiveRepository.save(new RawArchive(topic, content));
        log.info("Insight Raw Archived: ID={}", savedArchive.getId());

        // 2. 수식 전처리 (기존 기능 유지)
        String descriptiveContent = mathPreprocessor.preprocess(content);

        // 3. 벡터화 (Gemini 임베딩 생성)
        List<Double> embeddingVector = geminiService.getEmbedding(descriptiveContent);

        // 4. pgvector 포맷 변환
        String vectorString = embeddingVector.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));

        // 5. 시냅스 연결 및 저장 (가중치 1.0으로 시작)
        vectorNodeRepository.save(new VectorNode(savedArchive, vectorString));
        log.info("New Descriptive Insight Vectorized & Connected.");
    }
    /**
     * [회상] 가중치 기반 고성능 유사도 검색 (N+1 문제 해결 버전)
     */
    @Transactional(readOnly = true)
    public List<String> recallInsight(String vagueQuery) {
        // 1. 질문 벡터화
        List<Double> queryVector = geminiService.getEmbedding(vagueQuery);
        String vectorString = queryVector.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));

        // 2. [최적화] Native Join 검색 실행 (Object[] 반환)
        // 결과 배열 구조: [0]: VectorNode(v_), [1]: RawArchive(a_)
        List<Object[]> searchResults = vectorNodeRepository.findNearestNeighbors(vectorString, 3);

        // 3. 결과 매핑 및 반환 (지연 로딩 없이 즉시 접근)
        return searchResults.stream()
                .map(result -> {
                    RawArchive archive = (RawArchive) result[1]; // [1]번 인덱스에서 RawArchive 추출
                    return archive.getContent();
                })
                .collect(Collectors.toList());
    }
}