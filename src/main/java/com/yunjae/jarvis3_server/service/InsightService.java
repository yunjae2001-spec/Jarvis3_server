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
    private final MathPreprocessor mathPreprocessor; // [신규] 수식 해석기 주입

    /**
     * [저장] 수식을 해석하여 고밀도 설명적 임베딩 생성 및 저장
     */
    @Transactional
    public void digestInsight(String topic, String content) {
        // 1. 원본 저장 (Single Source of Truth: 사용자가 쓴 그대로 저장)
        RawArchive savedArchive = rawArchiveRepository.save(new RawArchive(topic, content));
        log.info("Insight Raw Archived: ID={}", savedArchive.getId());

        // 2. [신규] 수식 전처리 (벡터화 전용 텍스트 생성)
        // 예: "$SoC$" -> "배터리 충전 상태(SoC)"를 포함한 설명문으로 확장
        String descriptiveContent = mathPreprocessor.preprocess(content);

        // 3. 벡터화 (해석된 텍스트로 임베딩 생성)
        List<Double> embeddingVector = geminiService.getEmbedding(descriptiveContent);

        // 4. pgvector 포맷 변환 ("[0.1,0.2,...]")
        String vectorString = embeddingVector.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));

        // 5. 시냅스 연결 및 저장 (3072차원)
        vectorNodeRepository.save(new VectorNode(savedArchive, vectorString));
        log.info("Descriptive Insight Vectorized & Connected.");
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