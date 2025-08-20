package com.immoservice.immobiliers.repository;

import com.immoservice.immobiliers.entities.ActeNotarial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActeNotarialRepository extends JpaRepository<ActeNotarial, Long> {
    List<ActeNotarial> findByTransactionId(Long transactionId);
}
