package com.immoservice.immobiliers.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.immoservice.immobiliers.entities.BiensImmobiliers;
import com.immoservice.immobiliers.entities.TypeBiensImmobiliere;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.services.BiensImmobiliersService;
import com.immoservice.immobiliers.services.NoteBienService;
import com.immoservice.immobiliers.services.UserInfoService;

@RestController
@RequestMapping("/biens")
public class BiensImmobiliersController {

    @Autowired
    private BiensImmobiliersService biensImmobiliersService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private NoteBienService noteBienService;

    // Ajouter un bien
    // @PreAuthorize("hasAuthority('CREATE_BIENSIMMOBILIERS')")
    @PostMapping(value = "/add", consumes = "application/json")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> create(@RequestBody BiensImmobiliers bien,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        UserInfo user = userInfoService.findByUsername(userDetails.getUsername());
        System.out.println("Utilisateur connecté : " + user);
        bien.setCreatedBy(user); // 👈 définit le créateur
        BiensImmobiliers savedBien = biensImmobiliersService.save(bien);
        System.out.println("reponse après enregistrement : " + savedBien);
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

    @GetMapping("/disponibles")
    public ResponseEntity<List<BiensImmobiliers>> getDisponibles() {
        List<BiensImmobiliers> disponibles = biensImmobiliersService.getDisponibles();
        return ResponseEntity.ok(disponibles);
    }

    // Obtenir un bien par son ID
    @GetMapping("/get/{id}")
    public ResponseEntity<BiensImmobiliers> getById(@PathVariable Long id) {
        return biensImmobiliersService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Supprimer un bien
    // @PreAuthorize("hasAuthority('DELETE_BIENSIMMOBILIERS')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        biensImmobiliersService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Mettre à jour un bien
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

    @GetMapping("/user/biens")
    public ResponseEntity<Page<BiensImmobiliers>> getBiensByUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Récupérer l'utilisateur connecté
        UserInfo user = userInfoService.findByUsername(userDetails.getUsername());

        // Si l'utilisateur est null, renvoyer une erreur 404
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Récupérer les biens associés à l'utilisateur
        Page<BiensImmobiliers> biens = biensImmobiliersService.getBiensByUser(user, page, size);
        return ResponseEntity.ok(biens);
    }

    // ✅ GET /api/biens/moi?page=0&size=10
    @GetMapping("/moi")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<BiensImmobiliers>> getMesBiens(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BiensImmobiliers> biens = biensImmobiliersService.getBiensByCurrentUser(page, size);
        return ResponseEntity.ok(biens);
    }
}
