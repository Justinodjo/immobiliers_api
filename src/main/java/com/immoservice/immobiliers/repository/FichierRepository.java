package com.immoservice.immobiliers.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.Fichier;

@Repository
public interface FichierRepository extends JpaRepository<Fichier, Long> {
    Optional<Fichier> findByNom(String nom);
}
