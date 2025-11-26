// OffresController.java
package com.immoservice.immobiliers.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.immoservice.immobiliers.entities.Offres;
import com.immoservice.immobiliers.services.OffresService;

@RestController
@RequestMapping("/api/offres")
@CrossOrigin(origins = "*")
public class OffresController {

    @Autowired
    private OffresService offresService;

    @GetMapping("/all")
    public ResponseEntity<List<Offres>> getAllOffres() {
        return ResponseEntity.ok(offresService.getAllOffres());
    }

    @GetMapping("/actives")
    public ResponseEntity<Page<Offres>> getOffresActives(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        return ResponseEntity.ok(offresService.getOffresActives(PageRequest.of(page, size)));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Offres> getOffre(@PathVariable Long id) {
        return ResponseEntity.ok(offresService.getOffreById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<Offres> createOffre(@RequestBody Offres offre) {
        return ResponseEntity.ok(offresService.createOffre(offre));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Offres> updateOffre(@PathVariable Long id, @RequestBody Offres offre) {
        return ResponseEntity.ok(offresService.updateOffre(id, offre));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOffre(@PathVariable Long id) {
        offresService.deleteOffre(id);
        return ResponseEntity.ok().build();
    }
}