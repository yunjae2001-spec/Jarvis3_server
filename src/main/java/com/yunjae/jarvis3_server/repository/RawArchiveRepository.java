package com.yunjae.jarvis3_server.repository;

import com.yunjae.jarvis3_server.domain.RawArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RawArchiveRepository extends JpaRepository<RawArchive, Long> {

    // 나중에 특정 주제로 대화 로그를 찾을 때 사용
    List<RawArchive> findByTopicContaining(String topic);

    // 가장 최근의 대화 로그부터 가져오기
    List<RawArchive> findAllByOrderByCreatedAtDesc();
}
