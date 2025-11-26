package com.immoservice.immobiliers.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.immoservice.immobiliers.services.SparkBiensService;
import com.immoservice.immobiliers.services.SparkBiensService.ModelePredictionResult;

@RestController
@RequestMapping("/api/spark")
@ConditionalOnProperty(name = "spark.enabled", havingValue = "true", matchIfMissing = true)
public class SparkController {

    private static final Logger log = LoggerFactory.getLogger(SparkController.class);

    @Autowired
    private SparkBiensService sparkBiensService;

    /**
     * Endpoint pour analyser les prix moyens par type de bien
     * GET /api/spark/analyse-prix
     */
    @GetMapping("/analyse-prix")
    public ResponseEntity<?> analysePrixParType() {
        log.info("Requête reçue : analyse des prix par type de bien");

        try {
            Map<String, Double> resultat = sparkBiensService.analysePrixParType();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Analyse terminée avec succès");
            response.put("data", resultat);
            response.put("count", resultat.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de l'analyse des prix", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erreur lors de l'analyse des prix");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    /**
     * Endpoint pour entraîner le modèle de prédiction des prix
     * POST /api/spark/entrainer-modele
     */
    @PostMapping("/entrainer-modele")
    public ResponseEntity<?> entrainerModele() {
        log.info("Requête reçue : entraînement du modèle de prédiction");

        try {
            ModelePredictionResult resultat = sparkBiensService.entrainerModelePredictionPrix();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Modèle entraîné avec succès");
            response.put("data", Map.of(
                    "coefficients", resultat.getCoefficients(),
                    "intercept", resultat.getIntercept(),
                    "metrics", Map.of(
                            "rmse", resultat.getRmse(),
                            "r2", resultat.getR2(),
                            "mae", resultat.getMae()),
                    "dataset", Map.of(
                            "training_size", resultat.getTailleEntrainement(),
                            "test_size", resultat.getTailleTest())));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de l'entraînement du modèle", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erreur lors de l'entraînement du modèle");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    /**
     * Endpoint de santé pour vérifier que Spark fonctionne
     * GET /api/spark/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> checkHealth() {
        log.info("Vérification de la santé de Spark");

        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Spark est opérationnel");
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de la vérification de santé", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Spark n'est pas disponible");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(errorResponse);
        }
    }
}