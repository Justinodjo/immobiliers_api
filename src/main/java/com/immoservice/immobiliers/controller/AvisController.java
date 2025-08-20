package com.immoservice.immobiliers.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.immoservice.immobiliers.entities.Avis;
import com.immoservice.immobiliers.services.AvisService;

@RestController
@RequestMapping("/api/avis")
public class AvisController {
    private final AvisService avisService;

    public AvisController(AvisService avisService) {
        this.avisService = avisService;
    }

    @PostMapping("/add")
    public ResponseEntity<Avis> creer(@RequestBody Avis avis) {
        return ResponseEntity.ok(avisService.creerAvis(avis));
    }

    @GetMapping
    public ResponseEntity<List<Avis>> tous() {
        return ResponseEntity.ok(avisService.recupererTous());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avis> un(@PathVariable Long id) {
        return avisService.recupererParId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Avis> maj(@PathVariable Long id, @RequestBody Avis avis) {
        return ResponseEntity.ok(avisService.mettreAJour(id, avis));
    }
     @GetMapping("/bien/{id}")
    public List<Avis> getAvisParBien(@PathVariable Long id) {
        return avisService.recupererParBienId(id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        avisService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
