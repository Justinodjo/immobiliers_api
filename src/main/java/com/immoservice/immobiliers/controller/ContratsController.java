package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.entities.Contrats;
import com.immoservice.immobiliers.services.ContratsService;
import com.immoservice.immobiliers.repository.ContratsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contrats")
@CrossOrigin(origins = "*") // à adapter selon vos besoins CORS
public class ContratsController {

    @Autowired
    private ContratsService contratsService;

    @Autowired
    private ContratsRepository contratsRepository;

    // ✅ Créer un contrat
    @PostMapping("/add")
    public ResponseEntity<Contrats> createContrat(@RequestBody Contrats contrat,
            @RequestParam(required = false) String emailUtilisateur) {
        try {
            Contrats saved = contratsService.saveContrat(contrat, emailUtilisateur);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
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
    public ResponseEntity<byte[]> telechargerContratPDF(@PathVariable Long id) {
        Contrats contrat = contratsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat introuvable"));

        // Chemin absolu vers le logo (à adapter)
        String logoPath = "/chemin/vers/logo.png";

        byte[] pdfBytes = contratsService.genererContratPDF(contrat, logoPath);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contrat_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

}
