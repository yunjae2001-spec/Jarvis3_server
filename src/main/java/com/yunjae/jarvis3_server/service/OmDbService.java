package com.yunjae.jarvis3_server.service;

import com.yunjae.jarvis3_server.domain.OmDb;
import com.yunjae.jarvis3_server.repository.OmDbRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OmDbService {
    private final OmDbRepository repository;

    @Transactional
    public void updateWithFriction(Long id, String newContent, String reason) throws InterruptedException {
        OmDb db = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("데이터를 찾을 수 없습니다."));

        // Layer 1: 헌법 수정 시 10초 숙고 시간 (Friction)
        if (db.getLayerLevel() == 1) {
            log.warn("⚠️ 헌법 개정 시도. 10초간 대기합니다.");
            Thread.sleep(10000);
        }
        // Layer 2: 법률 수정 시 20자 이상 사유 체크
        else if (db.getLayerLevel() == 2 && (reason == null || reason.length() < 20)) {
            throw new RuntimeException("법률 개정 실패: 20자 이상의 사유가 필요합니다.");
        }

        // Entity에서 정의한 이름(updateContent)으로 호출!
        db.updateContent(newContent, reason);
    }

    public java.util.List<OmDb> getAllLayers() {
        return repository.findAllByOrderByLayerLevelAsc();
    }
}