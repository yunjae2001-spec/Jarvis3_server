package com.yunjae.jarvis3_server.repository;

import com.yunjae.jarvis3_server.domain.VectorNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VectorNodeRepository extends JpaRepository<VectorNode, Long> {

    /**
     * [핵심 기능] 벡터 유사도 검색 (Cosine Distance)
     * 입력된 벡터와 가장 거리가 가까운(의미가 비슷한) 순서대로 정렬하여 반환합니다.
     * <=> 연산자는 pgvector의 코사인 거리 연산자입니다.
     */
    @Query(value = "SELECT * FROM vector_nodes v " +
            "ORDER BY v.embedding <=> CAST(:embedding AS vector) " +
            "LIMIT :limit", nativeQuery = true)
    List<VectorNode> findNearestNeighbors(@Param("embedding") String embedding,
                                          @Param("limit") int limit);
}