package com.yunjae.jarvis3_server.repository;

import com.yunjae.jarvis3_server.domain.OmConstitution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OmConstitutionRepository extends JpaRepository<OmConstitution, Long> {
}