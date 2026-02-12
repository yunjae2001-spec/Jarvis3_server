package com.yunjae.jarvis3_server.repository;

import com.yunjae.jarvis3_server.domain.OmDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OmDbRepository extends JpaRepository<OmDb, Long> {
    // 레이어 순서대로 데이터를 가져오기 위한 쿼리
    List<OmDb> findAllByOrderByLayerLevelAsc();
}