package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.services.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @GetMapping("/contrat")
    public ResponseEntity<byte[]> getContratPdf(
            @RequestParam String nomClient,
            @RequestParam String typeContrat,
            @RequestParam String bien) throws IOException {

        // Chemin absolu du logo (optionnel, sinon mettre null)
        String logoPath = "src/main/resources/static/logo.png"; // Modifiez selon votre structure

        byte[] pdfBytes = pdfGeneratorService.generateContratAsBytes(nomClient, typeContrat, bien, logoPath);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contrat-" + nomClient + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
