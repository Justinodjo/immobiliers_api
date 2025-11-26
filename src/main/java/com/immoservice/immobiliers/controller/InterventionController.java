package com.immoservice.immobiliers.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import com.immoservice.immobiliers.entities.Intervention;
import com.immoservice.immobiliers.entities.StatutIntervention;
import com.immoservice.immobiliers.services.InterventionService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/interventions")
@CrossOrigin(origins = "*") // permet les requêtes depuis le frontend
public class InterventionController {

    @Autowired
    private InterventionService interventionService;

    // Récupérer toutes les interventions
    @GetMapping
    public ResponseEntity<List<Intervention>> getAllInterventions() {
        List<Intervention> interventions = interventionService.findAll();
        return ResponseEntity.ok(interventions);
    }

    // Récupérer une intervention par ID
    @GetMapping("/{id}")
    public ResponseEntity<Intervention> getInterventionById(@PathVariable Long id) {
        return interventionService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Intervention non trouvée avec l'ID : " + id));
    }

     /**
     * Créer une nouvelle intervention
     */
    @PostMapping("/create")
    public ResponseEntity<Intervention> createIntervention(@RequestBody Intervention intervention) {
        Intervention saved = interventionService.save(intervention);
        return ResponseEntity.ok(saved);
    }

    // Mettre à jour une intervention existante
    @PutMapping("/{id}")
    public ResponseEntity<Intervention> updateIntervention(@PathVariable Long id,
                                                           @RequestBody Intervention intervention) {
        Intervention updated = interventionService.update(id, intervention);
        return ResponseEntity.ok(updated);
    }

    // Supprimer une intervention
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIntervention(@PathVariable Long id) {
        interventionService.delete(id);
        return ResponseEntity.noContent().build();
    }


     /**
     * Récupérer les interventions par bien immobilier
     */
    @GetMapping("/bien/{bienId}")
    public ResponseEntity<List<Intervention>> getInterventionsByBien(@PathVariable Long bienId) {
        try {
            List<Intervention> interventions = interventionService.getInterventionsByBien(bienId);
            return ResponseEntity.ok(interventions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

      /**
     * Récupérer les interventions par statut
     */
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Intervention>> getInterventionsByStatut(@PathVariable StatutIntervention statut) {
        try {
            List<Intervention> interventions = interventionService.getInterventionsByStatut(statut);
            return ResponseEntity.ok(interventions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

      /**
     * Récupérer les interventions en attente
     */
    @GetMapping("/en-attente")
    public ResponseEntity<List<Intervention>> getInterventionsEnAttente() {
        try {
            List<Intervention> interventions = interventionService.getInterventionsEnAttente();
            return ResponseEntity.ok(interventions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

       /**
     * Récupérer les interventions en cours
     */
    @GetMapping("/en-cours")
    public ResponseEntity<List<Intervention>> getInterventionsEnCours() {
        try {
            List<Intervention> interventions = interventionService.getInterventionsEnCours();
            return ResponseEntity.ok(interventions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

      /**
     * Récupérer les statistiques des interventions
     */
    @GetMapping("/statistiques")
    public ResponseEntity<?> getStatistiquesInterventions() {
        try {
            return ResponseEntity.ok(interventionService.getStatistiques());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

      /**
     * Rechercher des interventions par type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Intervention>> getInterventionsByType(@PathVariable String type) {
        try {
            List<Intervention> interventions = interventionService.getInterventionsByType(type);
            return ResponseEntity.ok(interventions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}

