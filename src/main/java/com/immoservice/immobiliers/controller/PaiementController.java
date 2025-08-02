package com.immoservice.immobiliers.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.immoservice.immobiliers.services.PaiementService;

@RestController
@RequestMapping("/api/paiement")
public class PaiementController {
    private final PaiementService paiementService;

    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    @PostMapping("/initier")
    public ResponseEntity<Map<String, String>> initierPaiement(@RequestBody Map<String, Object> payload) {
        double montant = Double.parseDouble(payload.get("montant").toString());
        String email = payload.get("email").toString();
        String nom = payload.get("nom").toString();

        String urlPaiement = paiementService.initierPaiement(montant, email, nom);
        if (urlPaiement != null) {
            return ResponseEntity.ok(Map.of("url", urlPaiement));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur lors de l'initialisation du paiement"));
        }
    }
}
