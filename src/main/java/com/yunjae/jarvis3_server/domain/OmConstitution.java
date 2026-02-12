package com.yunjae.jarvis3_server.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "om_constitution")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OmConstitution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer layerLevel; // 1(절대원칙) ~ 5(세부수칙)

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Boolean isLocked;
}