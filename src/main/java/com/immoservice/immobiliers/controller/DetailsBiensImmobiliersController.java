package com.immoservice.immobiliers.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.immoservice.immobiliers.entities.DetailsBiensImmobiliers;
import com.immoservice.immobiliers.services.DetailsBiensImmobiliersService;

@RestController
@RequestMapping("/details_biens")
@CrossOrigin(origins = "*") // À adapter si vous avez des restrictions CORS
public class DetailsBiensImmobiliersController {
    @Autowired
    private DetailsBiensImmobiliersService detailsBiensImmobiliersService;

    // Ajouter un bien
    @PostMapping("/add/{bienId}")
    public ResponseEntity<DetailsBiensImmobiliers> create(@PathVariable Long bienId,
            @RequestBody DetailsBiensImmobiliers bien) {
        DetailsBiensImmobiliers savedBien = detailsBiensImmobiliersService.save(bien, bienId);
        return ResponseEntity.ok(savedBien);
    }

    // Lister tous les biens
    @GetMapping("/all")
    public ResponseEntity<List<DetailsBiensImmobiliers>> getAll() {
        return ResponseEntity.ok(detailsBiensImmobiliersService.getAll());
    }

    // Obtenir un bien par son ID
    @GetMapping("/get/{id}")
    public ResponseEntity<DetailsBiensImmobiliers> getById(@PathVariable Long id) {
        return detailsBiensImmobiliersService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Supprimer un bien
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        detailsBiensImmobiliersService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Mettre à jour un bien
    @PutMapping("/update/{id}")
    public ResponseEntity<DetailsBiensImmobiliers> updateDetailsBiensImmobiliers(@PathVariable Long id,
            @RequestBody DetailsBiensImmobiliers biens) {
        DetailsBiensImmobiliers updated = detailsBiensImmobiliersService.updateDetailsBiensImmobiliers(id, biens);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
