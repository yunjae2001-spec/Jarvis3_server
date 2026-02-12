package com.yunjae.jarvis3_server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "raw_archives")
public class RawArchive {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topic; // 대화 주제

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 정제된 Insight 내용

    private LocalDateTime createdAt;

    public RawArchive(String topic, String content) {
        this.topic = topic;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }
}