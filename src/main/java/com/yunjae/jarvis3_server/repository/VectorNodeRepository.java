package com.yunjae.jarvis3_server.repository;

import com.yunjae.jarvis3_server.domain.VectorNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VectorNodeRepository extends JpaRepository<VectorNode, Long> {

    /**
     * [N+1 문제 해결 및 가중치 인출 로직 통합]
     * - @SqlResultSetMapping("VectorNodeWithArchiveMapping")에 정의된 대로
     * VectorNode와 RawArchive 객체를 동시에 인출합니다.
     * - 가중치(synapse_strength)가 반영된 Native Query를 실행합니다.
     */
    // [중요] Mapping 설정에서 두 개의 @EntityResult를 정의했으므로 반환 타입은 List<Object[]>입니다.
    // 각 Object[]의 0번 인덱스에는 VectorNode, 1번 인덱스에는 RawArchive가 들어있습니다.
    List<Object[]> findNearestNeighbors(
            @Param("embedding") String embedding,
            @Param("limit") int limit
    );
}