package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.dto.MandatGestionDTO;
import com.immoservice.immobiliers.services.MandatGestionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mandat")
@CrossOrigin(origins = "*") // si ton front et ton back sont séparés
public class MandatGestionController {

    @Autowired
    private MandatGestionService mandatGestionService;

    /**
     * ⚙️ Endpoint pour mandater un gestionnaire sur une zone donnée
     */
    @PostMapping("/assigner")
    public ResponseEntity<?> mandaterGestionnaire(@RequestBody MandatGestionDTO dto) {
        try {
            int nombreBiens = mandatGestionService.mandaterGestionnaire(dto);
            return ResponseEntity.ok().body(
                String.format("✅ %d biens transférés au gestionnaire avec succès.", nombreBiens)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ Erreur : " + e.getMessage());
        }
    }

    /**
     * (Optionnel) Endpoint pour simuler / vérifier le mandat avant d’exécuter
     * → utile si tu veux afficher un récapitulatif au front avant validation
     */
    @PostMapping("/verifier")
    public ResponseEntity<?> verifierMandat(@RequestBody MandatGestionDTO dto) {
        try {
            int nombreBiens = mandatGestionService.simulerMandat(dto);
            return ResponseEntity.ok().body(
                String.format("ℹ️ %d biens seraient concernés par ce mandat.", nombreBiens)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ Erreur : " + e.getMessage());
        }
    }
}
