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

import com.immoservice.immobiliers.entities.Arrondissement;
import com.immoservice.immobiliers.services.ArrondissementService;

@RestController
@RequestMapping("/api/arrondissement")
public class ArrondissementConntroller {
    private final ArrondissementService arrondissementService;

    // @Autowired
    public ArrondissementConntroller(ArrondissementService arrondissementService) {
        this.arrondissementService = arrondissementService;
    }

    // Obtenir toutes les écoles
    @GetMapping("liste_arrondissements")
    public List<Arrondissement> getAllArrondissements() {
        return arrondissementService.getAllArrondissement();
    }

    // Obtenir une école par ID
    @GetMapping("/{idArrondissement}")
    public ResponseEntity<Arrondissement> getArrondissementById(@PathVariable Long idArrondissement) {
        Arrondissement arrondissement = arrondissementService.getArrondissementById(idArrondissement);
        if (arrondissement != null) {
            return ResponseEntity.ok(arrondissement);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Créer une nouvelle école
    @PostMapping("create_arrondissement")
    public ResponseEntity<Arrondissement> createArrondissement(@RequestBody Arrondissement arrondissement) {
        Arrondissement createdArrondissement = arrondissementService.createArrondissement(arrondissement);
        return ResponseEntity.ok(createdArrondissement);
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
    @DeleteMapping("delete/{idArrondissement}")
    public ResponseEntity<Void> deleteArrondissement(@PathVariable Long idArrondissement) {
        arrondissementService.deleteArrondissement(idArrondissement);
        return ResponseEntity.noContent().build();
    }

    // @GetMapping("/commune/{idCommune}/arrondissement")
    // public List<Arrondissement> getCommunesByDepartement(@PathVariable Long
    // idCommune) {
    // System.out.println(arrondissementService.getArrondissementsByCommuneId(idCommune));
    // return arrondissementService.getArrondissementsByCommuneId(idCommune);
    // }

    @GetMapping("/commune/{idCommune}")
    public List<Arrondissement> getArrondissementsByCommuneId(@PathVariable Long idCommune) {
        return arrondissementService.getArrondissementsByCommuneId(idCommune);
    }

    // @GetMapping("/commune/{libelle}")
    // public ResponseEntity<List<Arrondissement>> getCommunesByNomArrondissement(@PathVariable String libelle) {
    //     List<Arrondissement> arrondissements = arrondissementService.getArrondissementsByNomCommunes(libelle);
    //     return ResponseEntity.ok(arrondissements);
    // }
}
