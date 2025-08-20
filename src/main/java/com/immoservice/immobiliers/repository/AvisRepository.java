package com.immoservice.immobiliers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.immoservice.immobiliers.entities.Avis;

public interface AvisRepository extends JpaRepository<Avis, Long> {

    List<Avis> findByBiensImmobiliersId(Long id);

}
