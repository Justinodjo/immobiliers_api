package com.immoservice.immobiliers.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.immoservice.immobiliers.entities.Quartier;
import com.immoservice.immobiliers.services.QuartierService;

@RestController
@RequestMapping("/api/quartiers")
public class QuartierController {
    private final QuartierService quartierService;

    // @Autowired
    public QuartierController(QuartierService quartierService) {
        this.quartierService = quartierService;
    }

    // Obtenir toutes les écoles
    @GetMapping("liste_quartiers")
    public List<Quartier> getAllQuartiers() {
        return quartierService.getAllQuartiers();
    }

    // Obtenir une école par ID
    @GetMapping("rechercher/{idQuartier}")
    public ResponseEntity<Quartier> getQuartierById(@PathVariable Long idQuartier) {
        Quartier quartier = quartierService.getQuartierById(idQuartier);
        if (quartier != null) {
            return ResponseEntity.ok(quartier);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Créer une nouvelle école
    @PostMapping("create_quartier")
    public ResponseEntity<Quartier> createQuartier(@RequestBody Quartier quartier) {
        Quartier createdQuartier = quartierService.createQuartier(quartier);
        return ResponseEntity.ok(createdQuartier);
    }

    // Mettre à jour une école existante
    // @PutMapping("update/{idQuartier}")
    // public ResponseEntity<Quartier> mettreAJourQuartier(@PathVariable Long
    // idQuartier, @RequestBody Quartier quartierMaj) {
    // Quartier updatedQuartier = quartierService.mettreAJourQuartier(idQuartier,
    // quartierMaj);
    // return ResponseEntity.ok(updatedQuartier);
    // }

    // Supprimer une école par ID
    @DeleteMapping("delete/{idQuartier}")
    public ResponseEntity<Void> deleteQuartier(@PathVariable Long idQuartier) {
        quartierService.deleteQuartier(idQuartier);
        return ResponseEntity.noContent().build();
    }
}
