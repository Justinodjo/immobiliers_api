package com.immoservice.immobiliers.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.Offres;

@Repository
public interface OffresRepository extends JpaRepository<Offres, Long> {
    Page<Offres> findByDateDebutLessThanEqualAndDateFinGreaterThanEqual(
            Date dateDebut, Date dateFin, Pageable pageable);
}
