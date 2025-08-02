package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.entities.SousDetailsBiensImmobiliers;
import com.immoservice.immobiliers.services.SousDetailsBiensImmobiliersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sous-details-biens")
public class SousDetailsBiensImmobiliersController {

    @Autowired
    private SousDetailsBiensImmobiliersService service;

    @GetMapping
    public List<SousDetailsBiensImmobiliers> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SousDetailsBiensImmobiliers> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public SousDetailsBiensImmobiliers create(@RequestBody SousDetailsBiensImmobiliers sousDetailsBiensImmobiliers) {
        return service.save(sousDetailsBiensImmobiliers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SousDetailsBiensImmobiliers> update(@PathVariable Long id,
            @RequestBody SousDetailsBiensImmobiliers sousDetailsBiensImmobiliers) {
        try {
            return ResponseEntity.ok(service.update(id, sousDetailsBiensImmobiliers));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
