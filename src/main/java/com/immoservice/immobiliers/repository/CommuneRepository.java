package com.immoservice.immobiliers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.immoservice.immobiliers.entities.Commune;

@Repository
public interface CommuneRepository extends JpaRepository<Commune, Long> {

    // Rechercher les communes par l'ID du département
    // List<Commune> findByDepartement_IdDepartement(Long idDepartement);

    // Rechercher les communes par libellé (nom) de département
    List<Commune> findByDepartement_Libelle(String libelle);

    List<Commune> findByLibelleContainingIgnoreCase(String libelle);

}
