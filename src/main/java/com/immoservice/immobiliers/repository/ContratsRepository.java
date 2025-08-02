package com.immoservice.immobiliers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.Contrats;

@Repository
public interface ContratsRepository extends JpaRepository<Contrats, Long> {

    int countByBiensImmobiliers_Id(Long id);

}