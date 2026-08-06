package com.immoservice.immobiliers.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.immoservice.immobiliers.entities.Contrats;
import com.immoservice.immobiliers.entities.StatusContrat;
import com.immoservice.immobiliers.entities.TypeContrat;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.ContratsRepository;
import com.immoservice.immobiliers.services.ContratsService;
import com.immoservice.immobiliers.services.UserInfoService;

@RestController
@RequestMapping("/contrats")
@CrossOrigin(origins = "*") // à adapter selon vos besoins CORS
public class ContratsController {

    @Autowired
    private ContratsService contratsService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private ContratsRepository contratsRepository;

    // ✅ Créer un contrat
    @PostMapping("/add")
    public ResponseEntity<Contrats> createContrat(@RequestBody Contrats contrat,
            @RequestParam(required = false) String emailUtilisateur, @RequestParam boolean prixDetails) {
        try {
            // On récupère l'email de l’utilisateur connecté

            // Appel au service avec les 2 paramètres attendus
            Contrats savedContrat = contratsService.saveContrat(contrat, emailUtilisateur, prixDetails);

            return ResponseEntity.ok(savedContrat);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ Lister tous les contrats
    @GetMapping("/all")
    public ResponseEntity<List<Contrats>> getAllContrats() {
        return ResponseEntity.ok(contratsRepository.findAll());
    }

    // ✅ Obtenir un contrat par ID
    @GetMapping("/get/{id}")
    public ResponseEntity<Contrats> getContratById(@PathVariable Long id) {
        return contratsRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Supprimer un contrat (optionnel)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteContrat(@PathVariable Long id) {
        contratsRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/contrats/{id}/pdf")
    public ResponseEntity<byte[]> telechargerContratPDF(@PathVariable Long id) throws Exception {
        try {
            Contrats contrat = contratsRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Contrat introuvable"));

            // Chemin absolu vers le logo (à adapter)
            String logoPath = "/chemin/vers/logo.png";

            byte[] pdfBytes = contratsService.genererContratPDF(contrat, logoPath);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contrat_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (RuntimeException e) {
            // Si le contrat n'est pas trouvé
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Contrat avec l'ID " + id + " introuvable").getBytes());
        }
    }

    // ✅ Obtenir tous les biens immobiliers loués (contrat actif)
    @GetMapping("/biens-loues")
    public ResponseEntity<List<BiensImmobiliers>> getBiensImmobiliersLoues() {
        // Appel à la méthode du service
        List<BiensImmobiliers> biensLoues = contratsService.getBiensImmobiliersLoues();
        return ResponseEntity.ok(biensLoues);
    }

    @GetMapping("/contrats/{id}/verifier-documents")
    public Map<String, Boolean> verifierDocuments(@PathVariable Long id) {
        boolean tousDocumentsFournis = contratsService.verifierDocumentsContrat(id);
        return Map.of("tousDocumentsFournis", tousDocumentsFournis);
    }

    // ✅ Obtenir tous les biens immobiliers résiliés
    @GetMapping("/biens-resilies")
    public ResponseEntity<List<BiensImmobiliers>> getBiensImmobiliersResilies() {
        // Appel à la méthode du service
        List<BiensImmobiliers> biensResilies = contratsService.getBiensImmobiliersResilies();
        return ResponseEntity.ok(biensResilies);
    }

    @GetMapping("/recherche")
    public ResponseEntity<List<Contrats>> rechercherContrats(
            @RequestParam(required = false) TypeContrat type,
            @RequestParam(required = false) StatusContrat status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebutMin,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFinMax) {
        // Logique de valeurs par défaut pour la recherche
        if (dateDebutMin == null) {
            dateDebutMin = LocalDate.now().minusYears(1); // Par exemple, rechercher depuis un an par défaut
        }
        if (dateFinMax == null) {
            dateFinMax = LocalDate.now().plusYears(1); // Par exemple, rechercher jusqu'à un an dans le futur
        }

        List<Contrats> contrats = contratsService.rechercherContrats(type, status, dateDebutMin, dateFinMax);
        return ResponseEntity.ok(contrats);
    }

    // Endpoint pour récupérer tous les biens loués par un utilisateur spécifique
    @GetMapping("/biens-loues/{userId}")
    public ResponseEntity<List<BiensImmobiliers>> getBiensLouesParUtilisateur() {

        try {
            UserInfo user = contratsService.resolveUtilisateur(new Contrats());
            List<BiensImmobiliers> biens = contratsService.getBiensLouesParUtilisateur(user);
            return ResponseEntity.ok(biens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Contrats> updateContrat(@PathVariable Long id, @RequestBody Contrats contrat) {
        try {
            Contrats existingContrat = contratsRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Contrat introuvable"));

            existingContrat.setDateDebut(contrat.getDateDebut());
            existingContrat.setDateFin(contrat.getDateFin());
            existingContrat.setStatus(contrat.getStatus());
            existingContrat.setPrixTotal(contrat.getPrixTotal());
            existingContrat.setTypeContrat(contrat.getTypeContrat());

            Contrats updatedContrat = contratsRepository.save(existingContrat);
            return ResponseEntity.ok(updatedContrat);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/annuler/{id}")
    public ResponseEntity<String> annulerContrat(@PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateAnnulation) {
        try {
            contratsService.annulerContrat(id, dateAnnulation);
            return ResponseEntity.ok("Contrat annulé avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrat non trouvé");
        }
    }

    @PutMapping("/renouveler/{id}")
    public ResponseEntity<Contrats> renouvelerContrat(@PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nouvelleDateFin) {
        try {
            Contrats renouvelledContrat = contratsService.renouvelerContrat(id, nouvelleDateFin);
            return ResponseEntity.ok(renouvelledContrat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/archiver-expired")
    public ResponseEntity<String> archiverContratsExpirés() {
        try {
            contratsService.archiverContratsExpires();
            return ResponseEntity.ok("Contrats expirés archivés avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'archivage des contrats expirés");
        }
    }

    @GetMapping("/user/{email}/contrats")
    public ResponseEntity<List<Contrats>> getContratsByUserEmail(@PathVariable String email) {
        List<Contrats> contrats = contratsService.findByUserInfoEmail(email);
        if (contrats.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.ok(contrats);
    }

    @GetMapping("/moyenne-loyer-mensuel")
    public ResponseEntity<Double> getMoyenneLoyerMensuel() {
        double moyenne = contratsService.moyenneLoyerMensuel();
        return ResponseEntity.ok(moyenne);
    }

    @GetMapping("/nombre-contrats-actifs")
    public ResponseEntity<Long> getNombreContratsActifs() {
        long count = contratsService.nombreContratsActifs();
        return ResponseEntity.ok(count);
    }

    @PutMapping("/resilier-expired")
    public ResponseEntity<String> resilierContratsExpires() {
        try {
            contratsService.resilierContratsExpires();
            return ResponseEntity.ok("Contrats expirés résiliés avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la résiliation des contrats expirés");
        }
    }

    // @PutMapping("/{id}/valider")
    // public ResponseEntity<Contrats> validerContrat(
    // @PathVariable Long id,
    // @RequestParam ValidationStatus statut) {
    // try {
    // Contrats updated = contratsService.mettreAJourStatutValidation(id, statut);
    // return ResponseEntity.ok(updated);
    // } catch (RuntimeException e) {
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    // }
    // }

    // @GetMapping("/proprietaire/{id}/contrats")
    // public ResponseEntity<List<Contrats>> getContratsByProprietaire(@PathVariable
    // Long id) {
    // List<Contrats> contrats = contratsService.findByProprietaireId(id);
    // if (contrats.isEmpty()) {
    // return ResponseEntity.noContent().build();
    // }
    // return ResponseEntity.ok(contrats);
    // }

    @GetMapping("/actifs")
    public ResponseEntity<List<Contrats>> getContratsActifs() {
        return ResponseEntity.ok(contratsService.getContratsActifs());
    }

    @GetMapping("/expires")
    public ResponseEntity<List<Contrats>> getContratsExpires() {
        return ResponseEntity.ok(contratsService.getContratsExpires());
    }

    @GetMapping("/attente-validation")
    public ResponseEntity<List<Contrats>> getContratsEnAttente() {
        return ResponseEntity.ok(contratsService.getContratsEnAttente());
    }

    @GetMapping("/{id}/verifier-disponibilite")
    public ResponseEntity<Boolean> verifierDisponibilite(@PathVariable Long id) {
        boolean disponible = contratsService.verifierDisponibiliteContrat(id);
        return ResponseEntity.ok(disponible);
    }

    @GetMapping("/{id}/facture")
    public ResponseEntity<byte[]> genererFacture(@PathVariable Long id) {
        try {
            byte[] pdfBytes = contratsService.genererFacturePDF(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facture_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/export-csv")
    public ResponseEntity<String> exportContratsToCSV(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebutMin,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFinMax,
            @RequestParam(required = false) TypeContrat typeContrat,
            @RequestParam(required = false) StatusContrat status) {
        String csvData = contratsService.exportContratsAsCSV(dateDebutMin, dateFinMax, typeContrat, status);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contrats.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csvData);
    }

    // Validation par gestionnaire
    @PutMapping("/{id}/confirmer-gestionnaire")
    public ResponseEntity<Contrats> confirmerParGestionnaire(@PathVariable Long id) {
        return ResponseEntity.ok(contratsService.confirmerContratParGestionnaire(id));
    }

    // Validation par propriétaire
    @PutMapping("/{id}/valider-proprietaire")
    public ResponseEntity<Contrats> validerParProprietaire(@PathVariable Long id) {
        return ResponseEntity.ok(contratsService.validerContratParProprietaire(id));
    }

    // Validation par locataire
    @PutMapping("/{id}/valider-locataire")
    public ResponseEntity<Contrats> validerParLocataire(@PathVariable Long id) {
        return ResponseEntity.ok(contratsService.validerContratParLocataire(id));
    }

    // Score utilisateur courant
    @GetMapping("/score-utilisateur")
    public ResponseEntity<Integer> getScoreUtilisateur() {
        UserInfo user = userInfoService.getCurrentAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(contratsService.calculerScoreUtilisateur(user));
    }

    // Revenu du gestionnaire courant
    @GetMapping("/revenu-gestionnaire")
    public ResponseEntity<Double> getRevenuGestionnaire() {
        UserInfo gestionnaire = userInfoService.getCurrentAuthenticatedUser();
        if (gestionnaire == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(contratsService.calculerRevenuGestionnaire(gestionnaire));
    }

    // Revenu utilisateur courant
    @GetMapping("/revenu-utilisateur")
    public ResponseEntity<Double> getRevenuUtilisateur() {
        UserInfo user = userInfoService.getCurrentAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(contratsService.calculerRevenuParUtilisateur(user));
    }

    @GetMapping("/user/id/{userId}/contrats")
    public ResponseEntity<List<Contrats>> getContratsByUserId(@PathVariable Integer userId) {
        UserInfo user = contratsService.getById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Contrats> contrats = contratsService.getContratsParUtilisateur(user);
        return ResponseEntity.ok(contrats);
    }

}
