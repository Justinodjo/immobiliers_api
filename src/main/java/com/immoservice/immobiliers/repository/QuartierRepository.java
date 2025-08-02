package com.immoservice.immobiliers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.immoservice.immobiliers.entities.Quartier;

@Repository
public interface QuartierRepository extends JpaRepository<Quartier, Long> {

}