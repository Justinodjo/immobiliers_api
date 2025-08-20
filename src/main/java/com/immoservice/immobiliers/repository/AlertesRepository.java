package com.immoservice.immobiliers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.Alertes;

@Repository
public interface AlertesRepository extends JpaRepository<Alertes, Long> {

}