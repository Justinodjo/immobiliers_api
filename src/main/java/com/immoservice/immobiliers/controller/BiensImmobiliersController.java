package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.entities.BiensImmobiliers;
import com.immoservice.immobiliers.services.BiensImmobiliersService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/biens")
public class BiensImmobiliersController {

    @Autowired
    private BiensImmobiliersService biensImmobiliersService;

    // Ajouter un bien
    @PostMapping("/add")
    public ResponseEntity<BiensImmobiliers> create(@RequestBody BiensImmobiliers bien) {
        BiensImmobiliers savedBien = biensImmobiliersService.save(bien);
        return ResponseEntity.ok(savedBien);
    }

    // Lister tous les biens
    @GetMapping("/all")
    public ResponseEntity<List<BiensImmobiliers>> getAll() {
        return ResponseEntity.ok(biensImmobiliersService.getAll());
    }

    // Obtenir un bien par son ID
    @GetMapping("/get/{id}")
    public ResponseEntity<BiensImmobiliers> getById(@PathVariable Long id) {
        return biensImmobiliersService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Supprimer un bien
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        biensImmobiliersService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Mettre à jour un bien
    @PutMapping("/update/{id}")
    public ResponseEntity<BiensImmobiliers> update(@PathVariable Long id, @RequestBody BiensImmobiliers bien) {
        BiensImmobiliers updated = biensImmobiliersService.updateBiensImmobiliers(id, bien);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
