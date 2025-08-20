package com.immoservice.immobiliers.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.immoservice.immobiliers.entities.DetailsBiensImmobiliers;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailsBiensImmobiliersRepository extends JpaRepository<DetailsBiensImmobiliers, Long> {

}
