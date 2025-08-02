package com.immoservice.immobiliers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.Arrondissement;

@Repository
public interface ArrondissementRepository extends JpaRepository<Arrondissement, Long> {

    // List<Arrondissement> findByCommune_IdCommune(Long idCommune);

    List<Arrondissement> findByCommune_Libelle(String libelle);

    List<Arrondissement> findByLibelleContainingIgnoreCase(String libelle);

}
