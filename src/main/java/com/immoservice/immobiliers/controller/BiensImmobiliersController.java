package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.entities.BiensImmobiliers;
import com.immoservice.immobiliers.entities.TypeBiensImmobiliere;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.services.BiensImmobiliersService;
import com.immoservice.immobiliers.services.NoteBienService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/biens")
public class BiensImmobiliersController {

    @Autowired
    private BiensImmobiliersService biensImmobiliersService;

    @Autowired
    private NoteBienService noteBienService;

    // Ajouter un bien
    @PreAuthorize("hasAuthority('CREATE_BIENSIMMOBILIERS')")
    @PostMapping("/add")
    public ResponseEntity<BiensImmobiliers> create(@RequestBody BiensImmobiliers bien) {
        BiensImmobiliers savedBien = biensImmobiliersService.save(bien);
        return ResponseEntity.ok(savedBien);
    }

    // Lister tous les biens
    @GetMapping("/all")
    public Page<BiensImmobiliers> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        return biensImmobiliersService.getAll(page, size);
    }
@GetMapping("/search")
public List<BiensImmobiliers> searchBiens(
        @RequestParam(required = false) String texte,
        @RequestParam(required = false) Long type) {
    return biensImmobiliersService.searchBiensByTextAndType(texte, type);
}
    // Obtenir un bien par son ID
    @GetMapping("/get/{id}")
    public ResponseEntity<BiensImmobiliers> getById(@PathVariable Long id) {
        return biensImmobiliersService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Supprimer un bien
    @PreAuthorize("hasAuthority('DELETE_BIENSIMMOBILIERS')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        biensImmobiliersService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Mettre à jour un bien
    @PreAuthorize("hasAuthority('UPDATE_BIENSIMMOBILIERS')")
    @PutMapping("/update/{id}")
    public ResponseEntity<BiensImmobiliers> update(@PathVariable Long id, @RequestBody BiensImmobiliers bien) {
        BiensImmobiliers updated = biensImmobiliersService.updateBiensImmobiliers(id, bien);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/vues/{id}")
    public ResponseEntity<BiensImmobiliers> getBienAvecVue(@PathVariable Long id) {
        BiensImmobiliers bien = biensImmobiliersService.consulterBien(id);
        return ResponseEntity.ok(bien);
    }

    @PostMapping("/noter/{id}")
    public ResponseEntity<?> noterBien(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        int note = (int) body.get("note");

        // Récupération de l'utilisateur (si vous avez un système de sécurité en place)
        Integer utilisateurId = getUserIdFromUserDetails(userDetails); // à adapter selon votre sécurité

        noteBienService.noterBien(id, utilisateurId, note);
        return ResponseEntity.ok().build();
    }

    private Integer getUserIdFromUserDetails(UserDetails userDetails) {
        // Implémenter selon votre système d’authentification
        return ((UserInfo) userDetails).getId();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> ajouterLike(@PathVariable Long id) {
        try {
            return biensImmobiliersService.incrementerJaime(id)
                    .map(bien -> {
                        Map<String, Object> result = new HashMap<>();
                        result.put("likes", bien.getNombreJaime());
                        return ResponseEntity.ok(result);
                    })
                    .orElseGet(() -> {
                        Map<String, Object> error = new HashMap<>();
                        error.put("error", "Bien immobilier non trouvé pour l'ID : " + id);
                        return ResponseEntity.status(404).body(error);
                    });
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur serveur : " + e.getMessage());
            // Optionnel : logger l'erreur
            e.printStackTrace();
            return ResponseEntity.status(500).body(error);
        }

    }

    @GetMapping("/like/{id}")
    public ResponseEntity<Map<String, Object>> getLikes(@PathVariable Long id) {
        return biensImmobiliersService.getById(id)
                .map(bien -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("likes", bien.getNombreJaime());
                    return ResponseEntity.ok(result);
                })
                .orElse(ResponseEntity.notFound().build());
    }

     // Récupérer les biens par type avec pagination
    @GetMapping("/by-type")
    public Page<BiensImmobiliers> getBiensByType(
            @RequestParam Long typeId, 
            @RequestParam int page, 
            @RequestParam int size) {

        // Vous devez récupérer le TypeBiensImmobiliere par ID
        TypeBiensImmobiliere type = new TypeBiensImmobiliere();
        type.setId(typeId);

        // Appel du service pour récupérer les biens par type
        return biensImmobiliersService.getBiensByType(type, page, size);
    }
}
