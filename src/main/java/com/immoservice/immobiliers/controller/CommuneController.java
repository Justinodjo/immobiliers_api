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

import com.immoservice.immobiliers.entities.Commune;
import com.immoservice.immobiliers.services.CommuneService;
import com.immoservice.immobiliers.services.DepartementService;

@RestController
@RequestMapping("/api/commune")
public class CommuneController {
    private final CommuneService communeService;
    @SuppressWarnings("unused")
    private final DepartementService departementService;

    // @Autowired
    public CommuneController(CommuneService communeService, DepartementService departementService) {
        this.communeService = communeService;
        this.departementService = departementService;
    }

    // Obtenir toutes les écoles
    @GetMapping("liste_Communes")
    public List<Commune> getAllCommunes() {
        return communeService.getAllCommunes();
    }

    // Obtenir une école par ID
    @GetMapping("/{idCommune}")
    public ResponseEntity<Commune> getCommuneById(@PathVariable Long idCommune) {
        Commune commune = communeService.getCommuneById(idCommune);
        if (commune != null) {
            return ResponseEntity.ok(commune);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Créer une nouvelle école
    @PostMapping("create_commune")
    public ResponseEntity<Commune> createCommune(@RequestBody Commune commune) {
        Commune createdCommune = communeService.createCommune(commune);
        return ResponseEntity.ok(createdCommune);
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
    @DeleteMapping("delete/{idCommune}")
    public ResponseEntity<Void> deleteCommune(@PathVariable Long idCommune) {
        communeService.deleteCommune(idCommune);
        return ResponseEntity.noContent().build();
    }
    // @GetMapping("/{idCommune}/arrondissements")
    // public List<Arrondissement> getArrondissementsByCommune(@PathVariable Long
    // idCommune) {
    // return communeService.getArrondissementsByCommuneId(idCommune);
    // }

    // @GetMapping("/departement/{idDepartement}/communes")
    // public List<Commune> getCommunesByDepartement(@PathVariable Long
    // idDepartement) {
    // System.out.println(communeService.getCommunesByDepartementId(idDepartement));
    // return communeService.getCommunesByDepartementId(idDepartement);
    // }

    // Récupérer les communes par nom de département
    // @GetMapping("/departement/{libelle}")
    // public ResponseEntity<List<Commune>> getCommunesByNomDepartement(@PathVariable String libelle) {
    //     List<Commune> communes = communeService.getCommunesByNomDepartement(libelle);
    //     return ResponseEntity.ok(communes);
    // }

    @GetMapping("/departement/{idDepartement}")
    public List<Commune> getCommunesByDepartement(@PathVariable("idDepartement") Long departementId) {
        return communeService.getCommunesByDepartement(departementId);
    }
}