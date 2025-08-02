package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.entities.TypeBiensImmobiliere;
import com.immoservice.immobiliers.services.TypeBiensImmobiliereService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/typeBiens")
@CrossOrigin(origins = "*") // à adapter selon vos besoins
public class TypeBiensImmobiliereController {

    @Autowired
    private TypeBiensImmobiliereService service;

    // Ajouter un type de bien
    @PostMapping("/add")
    public ResponseEntity<TypeBiensImmobiliere> create(@RequestBody TypeBiensImmobiliere type) {
        return ResponseEntity.ok(service.saveTypeBiensImmobiliere(type));
    }

    // Lister tous les types
    @GetMapping("/all")
    public ResponseEntity<List<TypeBiensImmobiliere>> getAll() {
        return ResponseEntity.ok(service.getAllTypeBiensImmobilieres());
    }

    // Obtenir un type par ID
    @GetMapping("/get/{id}")
    public ResponseEntity<TypeBiensImmobiliere> getById(@PathVariable Long id) {
        return service.getTypeBiensImmobiliereById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Supprimer un type par ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteTypeBiensImmobiliere(id);
        return ResponseEntity.noContent().build();
    }

    // Mettre à jour un type
    @PutMapping("/update/{id}")
    public ResponseEntity<TypeBiensImmobiliere> update(@PathVariable Long id, @RequestBody TypeBiensImmobiliere type) {
        return ResponseEntity.ok(service.updateTypeBiensImmobiliere(id, type));
    }

    // Obtenir tous les types liés à un bien immobilier
    // @GetMapping("/biens/{biensImmobiliersId}")
    // public ResponseEntity<List<TypeBiensImmobiliere>> getByBiensId(@PathVariable
    // Long biensImmobiliersId) {
    // return
    // ResponseEntity.ok(service.getTypeBiensImmobilieresByCategorieId(biensImmobiliersId));
    // }
}
