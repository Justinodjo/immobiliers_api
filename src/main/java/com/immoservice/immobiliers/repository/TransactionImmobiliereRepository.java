package com.immoservice.immobiliers.repository;

import com.immoservice.immobiliers.entities.TransactionImmobiliere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionImmobiliereRepository extends JpaRepository<TransactionImmobiliere, Long> {
    List<TransactionImmobiliere> findByTypeTransaction(String typeTransaction);

}