package com.yunjae.jarvis3_server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;
import java.time.LocalDateTime; // [추가] 시간 기록용

@Entity
@Table(name = "vector_nodes")
@Getter
@Setter
@NoArgsConstructor
@NamedNativeQuery(
        name = "VectorNode.findNearestNeighbors",
        resultSetMapping = "VectorNodeWithArchiveMapping",
        query = """
        SELECT 
            v.id AS v_id, 
            v.embedding AS v_embedding,
            v.synapse_strength AS v_synapse_strength,
            v.archive_id AS v_archive_id,
            
            a.id AS a_id,
            a.topic AS a_topic,
            a.content AS a_content,
            a.created_at AS a_created_at
            
        FROM vector_nodes v
        INNER JOIN raw_archives a ON v.archive_id = a.id
        WHERE v.synapse_strength > 0 
        ORDER BY (v.embedding <=> CAST(:embedding AS vector)) / LN(v.synapse_strength + 1.0) ASC
        LIMIT :limit
    """
)
@SqlResultSetMapping(
        name = "VectorNodeWithArchiveMapping",
        entities = {
                @EntityResult(
                        entityClass = VectorNode.class,
                        fields = {
                                @FieldResult(name = "id", column = "v_id"),
                                @FieldResult(name = "embedding", column = "v_embedding"),
                                @FieldResult(name = "synapseStrength", column = "v_synapse_strength"),
                                @FieldResult(name = "archive", column = "v_archive_id")
                        }
                ),
                @EntityResult(
                        entityClass = RawArchive.class,
                        fields = {
                                @FieldResult(name = "id", column = "a_id"),
                                @FieldResult(name = "topic", column = "a_topic"),
                                @FieldResult(name = "content", column = "a_content"),
                                @FieldResult(name = "createdAt", column = "a_created_at")
                        }
                )
        }
)
public class VectorNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_id")
    private RawArchive archive;

    @Column(columnDefinition = "vector(3072)")
    @ColumnTransformer(
            read = "embedding::text",
            write = "CAST(? AS vector)"
    )
    private String embedding;

    @Column(name = "synapse_strength")
    private Double synapseStrength;

    // [추가] 마지막 활성화 시간: 스케줄러가 이 시간을 기준으로 망각을 결정합니다.
    @Column(name = "last_activated_at")
    private LocalDateTime lastActivatedAt = LocalDateTime.now();

    public VectorNode(RawArchive archive, String embedding) {
        this.archive = archive;
        this.embedding = embedding;
        this.synapseStrength = 1.0;
        this.lastActivatedAt = LocalDateTime.now(); // 생성 시점 초기화
    }

    /**
     * [추가] 시냅스 활성화: LIKE/Recall 발생 시 호출하여 망각 대상에서 제외시킵니다.
     */
    public void activateSynapse() {
        this.lastActivatedAt = LocalDateTime.now();
    }

    public void updateSynapse(boolean isPositive) {
        double learningRate = 0.1;
        if (isPositive) {
            this.synapseStrength += learningRate;
        } else {
            this.synapseStrength = Math.max(0.1, this.synapseStrength - learningRate);
        }
        this.activateSynapse(); // 가중치 변경도 일종의 '자극'이므로 시간 갱신
    }
}