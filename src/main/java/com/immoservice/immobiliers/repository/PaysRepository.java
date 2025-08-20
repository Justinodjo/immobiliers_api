package com.immoservice.immobiliers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.immoservice.immobiliers.entities.Pays;
import org.springframework.stereotype.Repository;
@Repository
public interface PaysRepository extends JpaRepository<Pays, Long> {
}
