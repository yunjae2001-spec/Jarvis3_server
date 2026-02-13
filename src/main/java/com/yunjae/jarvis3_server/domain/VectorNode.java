package com.yunjae.jarvis3_server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "vector_nodes")
@Getter
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
        
        /* [지능형 가중치 반영] 유사도 점수를 시냅스 강도로 나누어 우선순위 조정 */
        ORDER BY (v.embedding <=> CAST(:embedding AS vector)) / v.synapse_strength ASC
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
                                @FieldResult(name = "archive", column = "v_archive_id") // 관계 무결성 유지
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

    /* 기존 생성자 유지: 데이터 주입 시 충돌 방지 */
    public VectorNode(RawArchive archive, String embedding) {
        this.archive = archive;
        this.embedding = embedding;
        this.synapseStrength = 1.0; // 기본 가중치 초기화
    }

    /* [신규] 시냅스 강화/약화 로직 */
    public void updateSynapse(boolean isPositive) {
        double learningRate = 0.1; // 조정 폭은 수석님의 피드백 감도에 따라 조절 가능
        if (isPositive) {
            this.synapseStrength += learningRate;
        } else {
            this.synapseStrength = Math.max(0.1, this.synapseStrength - learningRate); // 최소값 방어선
        }
    }
}