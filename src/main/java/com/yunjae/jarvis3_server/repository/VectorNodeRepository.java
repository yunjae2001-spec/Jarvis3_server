package com.yunjae.jarvis3_server.repository;

import com.yunjae.jarvis3_server.domain.VectorNode;
import com.yunjae.jarvis3_server.domain.RawArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // [추가] 수정을 위한 어노테이션
import org.springframework.data.jpa.repository.Query;    // [추가] 벌크 쿼리 작성용
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime; // [추가] 시간 조건용
import java.util.List;
import java.util.Optional;

@Repository
public interface VectorNodeRepository extends JpaRepository<VectorNode, Long> {

    Optional<VectorNode> findByArchive(RawArchive archive);

    /**
     * [계층적 망각] 특정 토픽(레이어)에 속하면서 일정 기간 활성화되지 않은 시냅스를 일괄 감쇠시킵니다.
     * 수식: $Strength_{new} = Strength_{current} \times (1.0 - Rate)$
     */
    @Modifying(clearAutomatically = true)
    @Query("""
    UPDATE VectorNode v 
    SET v.synapseStrength = v.synapseStrength * (1.0 - :rate) 
    WHERE v.synapseStrength < :threshold  /* [보호막] 중요 지식은 감쇠 차단 */
    AND v.lastActivatedAt < :limitDate
    AND v.id IN (
        SELECT vn.id FROM VectorNode vn 
        JOIN vn.archive a 
        WHERE UPPER(a.topic) = UPPER(:topic) /* [정합성] 대소문자 무시 매칭 */
    )
""")
    void applyDecayWithProtection(@Param("topic") String topic,
                                  @Param("rate") double rate,
                                  @Param("limitDate") LocalDateTime limitDate,
                                  @Param("threshold") double threshold); // 매개변수 추가
    /**
     * [시냅스 소거] 가중치가 임계값(0.1) 미만으로 떨어진 노이즈성 기억을 물리적으로 삭제합니다.
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM VectorNode v WHERE v.synapseStrength < :minFloor")
    void purgeWeakSynapses(@Param("minFloor") double minFloor);

    /**
     * [기존 인출 로직 유지] 가중치가 반영된 Nearest Neighbor 검색
     */
    @Query(nativeQuery = true, name = "VectorNode.findNearestNeighbors")
    List<Object[]> findNearestNeighbors(
            @Param("embedding") String embedding,
            @Param("limit") int limit
    );
}