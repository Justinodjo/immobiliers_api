package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.entities.CaracteristiqueTechnique;
import com.immoservice.immobiliers.services.CaracteristiqueTechniqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/caracteristiques-techniques")
public class CaracteristiqueTechniqueController {

    @Autowired
    private CaracteristiqueTechniqueService service;

    @GetMapping
    public List<CaracteristiqueTechnique> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaracteristiqueTechnique> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CaracteristiqueTechnique create(@RequestBody CaracteristiqueTechnique caracteristique) {
        return service.save(caracteristique);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CaracteristiqueTechnique> update(@PathVariable Long id,
            @RequestBody CaracteristiqueTechnique updatedCaracteristique) {
        return service.findById(id)
                .map(existing -> {
                    updatedCaracteristique.setId(id);
                    return ResponseEntity.ok(service.save(updatedCaracteristique));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return service.findById(id)
                .map(caracteristique -> {
                    service.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
