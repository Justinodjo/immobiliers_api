package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.entities.Pays;
import com.immoservice.immobiliers.services.PaysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pays")
@CrossOrigin(origins = "*")
public class PaysController {

    @Autowired
    private PaysService paysService;

    // Ajouter un nouveau pays
    @PostMapping("/ajouter")
    public ResponseEntity<Pays> ajouterPays(@RequestBody Pays pays) {
        Pays nouveauPays = paysService.ajouterPays(pays);
        return ResponseEntity.ok(nouveauPays);
    }

    // Récupérer tous les pays
    @GetMapping("/liste")
    public ResponseEntity<List<Pays>> getAllPays() {
        return ResponseEntity.ok(paysService.getAllPays());
    }

    // Récupérer un pays par ID
    @GetMapping("/{id}")
    public ResponseEntity<Pays> getPaysById(@PathVariable Long id) {
        return ResponseEntity.ok(paysService.getPaysById(id));
    }

    // Mettre à jour un pays
    @PutMapping("/modifier/{id}")
    public ResponseEntity<Pays> updatePays(@PathVariable Long id, @RequestBody Pays pays) {
        return ResponseEntity.ok(paysService.updatePays(id, pays));
    }

    // Supprimer un pays
    @DeleteMapping("/supprimer/{id}")
    public ResponseEntity<Void> deletePays(@PathVariable Long id) {
        paysService.deletePays(id);
        return ResponseEntity.noContent().build();
    }
}
