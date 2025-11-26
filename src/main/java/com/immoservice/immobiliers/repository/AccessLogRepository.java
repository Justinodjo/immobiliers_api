package com.immoservice.immobiliers.repository;

import com.immoservice.immobiliers.entities.AccessLog;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    List<AccessLog> findByUtilisateur_Name(String name);

    List<AccessLog> findByAccessTimeBetween(LocalDateTime start, LocalDateTime end);
}
