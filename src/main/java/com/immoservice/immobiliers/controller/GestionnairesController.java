package com.immoservice.immobiliers.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.immoservice.immobiliers.dto.GestionnaireDTO;
import com.immoservice.immobiliers.entities.Contrats;
import com.immoservice.immobiliers.entities.StatusContrat;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.services.ContratsService;
import com.immoservice.immobiliers.services.UserInfoService;

@RestController
@RequestMapping("/api/gestionnaires")
@CrossOrigin(origins = "*")
public class GestionnairesController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ContratsService contratsService;

    /**
     * 📋 Obtenir tous les gestionnaires disponibles
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_ROLES', 'PROPRIETAIRE_ROLES', 'MANAGER_ROLES')")
    public ResponseEntity<List<GestionnaireDTO>> getAllGestionnaires() {
        try {
            List<UserInfo> gestionnaires = userInfoService.getAllGestionnaires();

            List<GestionnaireDTO> dtos = gestionnaires.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 🎯 Obtenir les gestionnaires recommandés pour un bien
     */
    @GetMapping("/recommandes/{bienId}")
    @PreAuthorize("hasAnyRole('ADMIN_ROLES', 'PROPRIETAIRE_ROLES')")
    public ResponseEntity<List<GestionnaireDTO>> getGestionnairesRecommandes(
            @PathVariable Long bienId) {
        try {
            List<UserInfo> gestionnaires = contratsService.getGestionnairesRecommandesPourBien(bienId);

            List<GestionnaireDTO> dtos = gestionnaires.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * 📍 Obtenir les gestionnaires par zone
     */
    @GetMapping("/par-zone")
    @PreAuthorize("hasAnyRole('ADMIN_ROLES', 'PROPRIETAIRE_ROLES')")
    public ResponseEntity<List<GestionnaireDTO>> getGestionnairesByZone(
            @RequestParam String adresse) {
        try {
            List<UserInfo> gestionnaires = contratsService.getGestionnairesByZone(adresse);

            List<GestionnaireDTO> dtos = gestionnaires.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 📊 Obtenir les statistiques d'un gestionnaire
     */
    @GetMapping("/{id}/statistiques")
    @PreAuthorize("hasAnyRole('ADMIN_ROLES', 'MANAGER_ROLES')")
    public ResponseEntity<Map<String, Object>> getStatistiquesGestionnaire(
            @PathVariable Integer id) {
        try {
            UserInfo gestionnaire = userInfoService.getUser(id);

            int nbContrats = contratsService.getContratsByGestionnaire(id.longValue()).size();
            double revenu = contratsService.calculerRevenuGestionnaire(gestionnaire);
            int score = userInfoService.getUserScore(id);

            Map<String, Object> stats = Map.of(
                    "nbContratsGeres", nbContrats,
                    "revenuGenere", revenu,
                    "score", score,
                    "tauxReussite", calculerTauxReussite(gestionnaire));

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * ✅ Assigner un gestionnaire à un contrat
     */
    @PostMapping("/assigner")
    @PreAuthorize("hasAnyRole('ADMIN_ROLES', 'PROPRIETAIRE_ROLES')")
    public ResponseEntity<Map<String, String>> assignerGestionnaire(
            @RequestBody Map<String, Long> request) {
        try {
            Long contratId = request.get("contratId");
            Integer gestionnaireId = request.get("gestionnaireId").intValue();

            Contrats contrat = contratsService.getContratById(contratId);
            UserInfo gestionnaire = userInfoService.getUser(gestionnaireId);

            if (contrat == null || gestionnaire == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Contrat ou gestionnaire introuvable"));
            }

            contratsService.assignerGestionnaireEtCalculerCommission(contrat, gestionnaire);

            return ResponseEntity.ok(Map.of(
                    "success", "true",
                    "message", "Gestionnaire assigné avec succès",
                    "gestionnaire", gestionnaire.getNomComplet()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", "false", "message", e.getMessage()));
        }
    }

    // ============================================
    // MÉTHODES UTILITAIRES
    // ============================================

    /**
     * Convertir UserInfo en GestionnaireDTO
     */
    private GestionnaireDTO convertToDTO(UserInfo gestionnaire) {
        GestionnaireDTO dto = new GestionnaireDTO();

        dto.setId(gestionnaire.getId());
        dto.setNom(gestionnaire.getName());
        dto.setPrenom(gestionnaire.getSecondName());
        dto.setEmail(gestionnaire.getEmail());
        dto.setTelephone(gestionnaire.getNumero());
        dto.setAdresse(gestionnaire.getAdresse());
        dto.setFichierId(gestionnaire.getFichier() != null ? gestionnaire.getFichier().getId() : null);

        // Statistiques
        try {
            int nbContrats = contratsService.getContratsByGestionnaire(gestionnaire.getId().longValue()).size();
            double revenu = contratsService.calculerRevenuGestionnaire(gestionnaire);
            int score = userInfoService.getUserScore(gestionnaire.getId());

            dto.setNbContratsGeres(nbContrats);
            dto.setRevenuGenere(revenu);
            dto.setScore(score);
            dto.setTauxReussite(calculerTauxReussite(gestionnaire));
            dto.setDisponible(nbContrats < 10); // Max 10 contrats simultanés
        } catch (Exception e) {
            dto.setNbContratsGeres(0);
            dto.setRevenuGenere(0.0);
            dto.setScore(0);
            dto.setTauxReussite(0.0);
            dto.setDisponible(true);
        }

        return dto;
    }

    /**
     * Calculer le taux de réussite d'un gestionnaire
     */
    private double calculerTauxReussite(UserInfo gestionnaire) {
        List<Contrats> contrats = contratsService.getContratsByGestionnaire(
                gestionnaire.getId().longValue());

        if (contrats.isEmpty())
            return 100.0;

        long contratsReussis = contrats.stream()
                .filter(c -> c.getStatus() == StatusContrat.ACTIF ||
                        c.getStatus() == StatusContrat.TERMINE)
                .count();

        return (contratsReussis * 100.0) / contrats.size();
    }
}