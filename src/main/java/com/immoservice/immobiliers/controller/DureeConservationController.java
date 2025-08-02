package com.immoservice.immobiliers.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.immoservice.immobiliers.entities.BaseLegale;
import com.immoservice.immobiliers.entities.DureeConservation;
import com.immoservice.immobiliers.entities.TypeDonnee;
import com.immoservice.immobiliers.services.DureeConservationService;

@RestController
@RequestMapping("/api/durees-conservation")
public class DureeConservationController {

    private final DureeConservationService service;

    public DureeConservationController(DureeConservationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DureeConservation> create(@RequestBody DureeConservation duree) {
        return ResponseEntity.ok(service.save(duree));
    }

    @GetMapping
    public ResponseEntity<Page<DureeConservation>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.findAll(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DureeConservation> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-type")
    public ResponseEntity<Page<DureeConservation>> getByTypeDonnee(
            @RequestParam TypeDonnee typeDonnee,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.findByTypeDonnee(typeDonnee, PageRequest.of(page, size)));
    }

    @GetMapping("/by-base-legale")
    public ResponseEntity<Page<DureeConservation>> getByBaseLegale(
            @RequestParam BaseLegale baseLegale,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.findByBaseLegale(baseLegale, PageRequest.of(page, size)));
    }
}
