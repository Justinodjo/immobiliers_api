package com.immoservice.immobiliers.repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.immoservice.immobiliers.entities.StatutIntervention;
import com.immoservice.immobiliers.entities.Intervention;

@Repository
public interface InterventionRepository extends JpaRepository<Intervention, Long> {
  // 🔹 Récupérer toutes les interventions d’un bien immobilier
    List<Intervention> findByBiensImmobilersId(Long bienId);

    // 🔹 Récupérer les interventions par statut
    List<Intervention> findByStatut(StatutIntervention statut);

    // 🔹 Récupérer les interventions par type
    List<Intervention> findByTypeIntervention(String typeIntervention);

    // 🔹 Compter par statut (utile pour les statistiques)
    long countByStatut(StatutIntervention statut);

    // 🔹 Récupérer les interventions "en attente"
    @Query("SELECT i FROM Intervention i WHERE i.statut = 'EN_ATTENTE'")
    List<Intervention> findInterventionsEnAttente();

    // 🔹 Récupérer les interventions "en cours"
    @Query("SELECT i FROM Intervention i WHERE i.statut = 'EN_COURS'")
    List<Intervention> findInterventionsEnCours();
}