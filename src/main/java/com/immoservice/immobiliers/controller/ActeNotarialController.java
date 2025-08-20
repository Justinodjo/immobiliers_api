package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.entities.ActeNotarial;
import com.immoservice.immobiliers.services.ActeNotarialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actes")
public class ActeNotarialController {

    private final ActeNotarialService acteNotarialService;

    public ActeNotarialController(ActeNotarialService acteNotarialService) {
        this.acteNotarialService = acteNotarialService;
    }

    // Créer un acte lié à une transaction
    @PostMapping("/transaction/{transactionId}")
    public ResponseEntity<ActeNotarial> creerActe(
            @PathVariable Long transactionId,
            @RequestBody ActeNotarial acte) {
        return ResponseEntity.ok(acteNotarialService.creerActe(transactionId, acte));
    }

    // Récupérer tous les actes
    @GetMapping
    public ResponseEntity<List<ActeNotarial>> getTousLesActes() {
        return ResponseEntity.ok(acteNotarialService.getTousLesActes());
    }

    // Récupérer les actes d'une transaction
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<ActeNotarial>> getActesParTransaction(@PathVariable Long transactionId) {
        return ResponseEntity.ok(acteNotarialService.getActesParTransaction(transactionId));
    }

    // Récupérer un acte par ID
    @GetMapping("/{id}")
    public ResponseEntity<ActeNotarial> getActeParId(@PathVariable Long id) {
        return ResponseEntity.ok(acteNotarialService.getActeParId(id));
    }

    // Mettre à jour un acte
    @PutMapping("/{id}")
    public ResponseEntity<ActeNotarial> mettreAJourActe(@PathVariable Long id, @RequestBody ActeNotarial acte) {
        return ResponseEntity.ok(acteNotarialService.mettreAJourActe(id, acte));
    }

    // Supprimer un acte
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerActe(@PathVariable Long id) {
        acteNotarialService.supprimerActe(id);
        return ResponseEntity.noContent().build();
    }
}
