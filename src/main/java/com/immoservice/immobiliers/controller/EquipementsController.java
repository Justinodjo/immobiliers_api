package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.entities.Equipements;
import com.immoservice.immobiliers.services.EquipementsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipements")
public class EquipementsController {

    private final EquipementsService equipementsService;

    public EquipementsController(EquipementsService equipementsService) {
        this.equipementsService = equipementsService;
    }

    @PostMapping
    public ResponseEntity<Equipements> create(@RequestBody Equipements equipements) {
        return ResponseEntity.ok(equipementsService.save(equipements));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipements> getById(@PathVariable Long id) {
        return equipementsService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Equipements>> getAll() {
        return ResponseEntity.ok(equipementsService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipements> update(@PathVariable Long id, @RequestBody Equipements equipements) {
        return ResponseEntity.ok(equipementsService.update(id, equipements));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        equipementsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
