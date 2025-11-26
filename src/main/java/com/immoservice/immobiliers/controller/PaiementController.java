package com.immoservice.immobiliers.controller;

import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Hex;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.immoservice.immobiliers.entities.Paiements;
import com.immoservice.immobiliers.entities.StatutPaiement;
import com.immoservice.immobiliers.services.PaiementService;

@RestController
@RequestMapping("/api/paiements")
@SuppressWarnings("CallToPrintStackTrace")
public class PaiementController {

    private final PaiementService paiementService;
    private static final String API_KEY = "sk_live_uKcXC79oLvQReXGou2S691ps";

    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    /**
     * 🔹 Initier un paiement pour un contrat
     */
    @PostMapping("/initier")
    public ResponseEntity<Map<String, String>> initierPaiement(@RequestBody Map<String, Object> payload) {
        try {
            Long contratId = Long.valueOf(payload.get("contratId").toString());
            double montant = Double.parseDouble(payload.get("montant").toString());
            String email = payload.get("email").toString();
            String nom = payload.get("nom").toString();

            String urlPaiement = paiementService.initierPaiement(contratId, montant, email, nom);

            if (urlPaiement != null && urlPaiement.startsWith("http")) {
                return ResponseEntity.ok(Map.of("url", urlPaiement));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", urlPaiement));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur lors de l'initialisation du paiement"));
        }
    }

    /**
     * 🔹 Callback sécurisé FedaPay
     * Vérifie la signature HMAC-SHA256
     */
    @PostMapping("/callback")
    public ResponseEntity<String> callbackFedaPay(@RequestHeader("X-Fedapay-Signature") String signature,
            @RequestBody Map<String, Object> payload) {
        try {
            // Vérification de la signature
            if (!verifierSignature(payload, signature)) {
                System.err.println("⚠️ Signature FedaPay invalide !");
                return ResponseEntity.status(403).body("Signature invalide");
            }

            System.out.println("✅ Callback FedaPay reçu et authentifié : " + payload);
            paiementService.traiterCallback(payload);

            return ResponseEntity.ok("Callback traité avec succès");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erreur lors du traitement du callback");
        }
    }

    /**
     * 🔹 Vérifie la signature HMAC-SHA256 de FedaPay
     */
    private boolean verifierSignature(Map<String, Object> payload, String signature) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(API_KEY.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            // Convertir le payload en JSON string (assurez-vous d’utiliser le même format
            // qu’envoyé par FedaPay)
            String payloadJson = payload.toString(); // ou utiliser une librairie comme Jackson/Gson pour sérialiser
                                                     // correctement

            String hash = Hex.encodeHexString(sha256_HMAC.doFinal(payloadJson.getBytes()));
            return hash.equals(signature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Créer un nouveau paiement
     */
    @PostMapping("/create")
    public ResponseEntity<Paiements> createPaiement(@RequestBody Paiements paiement) {
        try {
            Paiements created = paiementService.createPaiement(paiement);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer tous les paiements
     */
    @GetMapping("/all")
    public ResponseEntity<List<Paiements>> getAllPaiements() {
        try {
            List<Paiements> paiements = paiementService.getAllPaiements();
            return ResponseEntity.ok(paiements);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer un paiement par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Paiements> getPaiementById(@PathVariable Long id) {
        try {
            Paiements paiement = paiementService.getPaiementById(id);
            return ResponseEntity.ok(paiement);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Mettre à jour un paiement
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Paiements> updatePaiement(
            @PathVariable Long id,
            @RequestBody Paiements paiement) {
        try {
            Paiements updated = paiementService.updatePaiement(id, paiement);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Supprimer un paiement
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePaiement(@PathVariable Long id) {
        try {
            paiementService.deletePaiement(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Marquer un paiement comme payé
     */
    @PutMapping("/{id}/marquer-paye")
    public ResponseEntity<Paiements> marquerPaye(
            @PathVariable Long id,
            @RequestParam String modePaiement,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datePaiement,
            @RequestParam(required = false) Double montantPaye,
            @RequestParam(required = false) String numeroTransaction) {
        try {
            Paiements updated = paiementService.marquerPaye(
                    id, modePaiement, datePaiement, montantPaye, numeroTransaction);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Marquer plusieurs paiements comme payés
     */
    @PutMapping("/marquer-payes-multiple")
    public ResponseEntity<List<Paiements>> marquerPayesMultiple(
            @RequestBody Map<String, Object> payload) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) payload.get("ids");
            String modePaiement = (String) payload.get("modePaiement");
            LocalDate datePaiement = LocalDate.parse((String) payload.get("datePaiement"));

            List<Paiements> updated = paiementService.marquerPayesMultiple(ids, modePaiement, datePaiement);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Envoyer un rappel de paiement
     */
    @PostMapping("/{id}/envoyer-rappel")
    public ResponseEntity<Void> envoyerRappel(@PathVariable Long id) {
        try {
            paiementService.envoyerRappel(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Envoyer plusieurs rappels
     */
    @PostMapping("/envoyer-rappels-multiple")
    public ResponseEntity<Void> envoyerRappelsMultiple(@RequestBody Map<String, List<Long>> payload) {
        try {
            List<Long> ids = payload.get("ids");
            paiementService.envoyerRappelsMultiple(ids);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer les paiements par statut
     */
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Paiements>> getPaiementsByStatut(@PathVariable StatutPaiement statut) {
        try {
            List<Paiements> paiements = paiementService.getPaiementsByStatut(statut);
            return ResponseEntity.ok(paiements);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer les paiements par locataire
     */
    @GetMapping("/locataire/{locataireId}")
    public ResponseEntity<List<Paiements>> getPaiementsByLocataire(@PathVariable Long locataireId) {
        try {
            List<Paiements> paiements = paiementService.getPaiementsByLocataire(locataireId);
            return ResponseEntity.ok(paiements);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Récupérer les paiements par bien
     */
    @GetMapping("/bien/{bienId}")
    public ResponseEntity<List<Paiements>> getPaiementsByBien(@PathVariable Long bienId) {
        try {
            List<Paiements> paiements = paiementService.getPaiementsByBien(bienId);
            return ResponseEntity.ok(paiements);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Récupérer les statistiques des paiements
     */
    @GetMapping("/statistiques")
    public ResponseEntity<Map<String, Object>> getStatistiques() {
        try {
            Map<String, Object> stats = paiementService.getStatistiques();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Exporter les paiements en Excel
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportPaiements(
            @RequestParam(required = false) Map<String, Object> filters) {
        try {
            byte[] excelData = paiementService.exportToExcel(filters);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=paiements_" +
                            LocalDate.now() + ".xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Vérifier les paiements en retard (endpoint admin)
     */
    @PostMapping("/verifier-retards")
    public ResponseEntity<Void> verifierPaiementsEnRetard() {
        try {
            paiementService.verifierPaiementsEnRetard();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
