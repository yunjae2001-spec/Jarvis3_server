package com.yunjae.jarvis3_server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "vector_nodes")
public class VectorNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_id")
    private RawArchive archive;

    /**
     * [Jarvis 시냅스 핵심 설정]
     * 자바에서는 String으로 편하게 다루고,
     * DB에 저장될 때 CAST(? AS vector)를 통해 강제 형변환을 수행합니다.
     */
    @Column(columnDefinition = "vector(3072)")
    @ColumnTransformer(
            read = "embedding::text",
            write = "CAST(? AS vector)"
    )
    private String embedding;

    private Double synapseStrength; // 0.0 ~ 1.0 (연결 강도)

    public VectorNode(RawArchive archive, String embedding) {
        this.archive = archive;
        this.embedding = embedding;
        this.synapseStrength = 1.0; // 초기 생성 시 강도 1.0
    }
}