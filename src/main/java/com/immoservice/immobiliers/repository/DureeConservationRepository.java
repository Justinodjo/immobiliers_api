package com.immoservice.immobiliers.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.BaseLegale;
import com.immoservice.immobiliers.entities.DureeConservation;
import com.immoservice.immobiliers.entities.TypeDonnee;

@Repository
public interface DureeConservationRepository extends JpaRepository<DureeConservation, Long> {
    Page<DureeConservation> findByTypeDonnee(TypeDonnee typeDonnee, Pageable pageable);

    Page<DureeConservation> findByBaseLegale(BaseLegale baseLegale, Pageable pageable);
}
