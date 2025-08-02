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

import com.immoservice.immobiliers.entities.Departement;
import com.immoservice.immobiliers.services.DepartementService;

@RestController
@RequestMapping("/api/departement")
public class DepartementConntroller {
    private final DepartementService departementService;

    // @Autowired
    public DepartementConntroller(DepartementService departementService) {
        this.departementService = departementService;
    }

    // Obtenir toutes les écoles
    @GetMapping("liste_departements")
    public List<Departement> getAllDepartements() {
        return departementService.getAllDepartements();
    }

    // Obtenir une école par ID
    @GetMapping("/{idDepartement}")
    public ResponseEntity<Departement> getDepartementById(@PathVariable Long idDepartement) {
        Departement departement = departementService.getDepartementById(idDepartement);
        if (departement != null) {
            return ResponseEntity.ok(departement);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Créer une nouvelle école
    @PostMapping("create_departement")
    public ResponseEntity<Departement> createDepartement(@RequestBody Departement departement) {
        Departement createdDepartement = departementService.createDepartement(departement);
        return ResponseEntity.ok(createdDepartement);
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
    @DeleteMapping("delete/{idDepartement}")
    public ResponseEntity<Void> deleteDepartement(@PathVariable Long idDepartement) {
        departementService.deleteDepartement(idDepartement);
        return ResponseEntity.noContent().build();
    }

    /*
     * @GetMapping("/{idDepartement}/communes")
     * public List<Commune> getCommunesByDepartement(@PathVariable Long
     * idDepartement) {
     * return departementService.getCommunesByDepartementId(idDepartement);
     * }
     */
}
