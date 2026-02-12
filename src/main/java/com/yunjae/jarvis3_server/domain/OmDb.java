package com.yunjae.jarvis3_server.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "om_db")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OmDb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer layerLevel;
    private String category;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String updateReason;
    private LocalDateTime updatedAt;

    // 네가 선호하는 메서드 명칭 유지
    @PreUpdate
    @PrePersist
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // 데이터 수정을 위한 메서드 명칭 유지
    public void updateContent(String content, String reason) {
        this.content = content;
        this.updateReason = reason;
    }
}