package com.immoservice.immobiliers.repository;

import com.immoservice.immobiliers.entities.AuditLog;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // List<AuditLog> findByUtilisateur_Username(String username);

    // List<AuditLog> findByAccessTimeBetween(LocalDateTime start, LocalDateTime end);
}