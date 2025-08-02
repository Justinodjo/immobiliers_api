package com.immoservice.immobiliers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.Intervention;

@Repository
public interface InterventionRepository extends JpaRepository<Intervention, Long> {

}