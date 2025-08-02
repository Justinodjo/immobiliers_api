package com.immoservice.immobiliers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.SousDetailsBiensImmobiliers;

@Repository
public interface SousDetailsBiensImmobiliersRepository extends JpaRepository<SousDetailsBiensImmobiliers, Long> {

}
