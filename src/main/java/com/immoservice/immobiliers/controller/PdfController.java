package com.immoservice.immobiliers.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.immoservice.immobiliers.services.PdfService;
import com.immoservice.immobiliers.services.pdf.GestionPdfGenerator;
import com.immoservice.immobiliers.services.pdf.MandatPdfGenerator;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private PdfService pdfGeneratorService;

    @GetMapping("/contrat")
    public ResponseEntity<byte[]> getContratPdf(
            @RequestParam String nomClient,
            @RequestParam String typeContrat,
            @RequestParam String bien) {

        // Chemin absolu du logo (optionnel)
        String logoPath = "src/main/resources/static/logo.png";

        try {
            // Préparer les données selon ton type de contrat
            Object data;
            switch (typeContrat.toUpperCase()) {
                case "GESTION":
                    GestionPdfGenerator.GestionData gestionData = new GestionPdfGenerator.GestionData();
                    gestionData.nomProprietaire = nomClient;
                    gestionData.adresseBien = bien;
                    gestionData.conditions = "Conditions générales";
                    data = gestionData;
                    break;
                case "MANDAT":
                    MandatPdfGenerator.MandatData mandatData = new MandatPdfGenerator.MandatData();
                    mandatData.nomSyndic = nomClient;
                    mandatData.adresseBien = bien;
                    mandatData.logoPath = logoPath;
                    data = mandatData;
                    break;
                default:
                    throw new IllegalArgumentException("Type de contrat inconnu : " + typeContrat);
            }

            byte[] pdfBytes = pdfGeneratorService.generate(typeContrat.toUpperCase(), data);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contrat-" + nomClient + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

}
