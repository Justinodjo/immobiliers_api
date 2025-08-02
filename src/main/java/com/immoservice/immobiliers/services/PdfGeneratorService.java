package com.immoservice.immobiliers.services;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

@Service
public class PdfGeneratorService {

    public byte[] generateContratAsBytes(String nomClient, String typeContrat, String bien, String logoPath)
            throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                // Affichage du logo si disponible
                if (logoPath != null && !logoPath.isEmpty()) {
                    PDImageXObject pdImage = PDImageXObject.createFromFile(logoPath, document);
                    contentStream.drawImage(pdImage, 50, 700, 100, 50); // x, y, width, height
                }

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.setLeading(20f);
                contentStream.newLineAtOffset(50, 650);

                contentStream.showText("CONTRAT DE " + typeContrat.toUpperCase());
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLine();
                contentStream.showText("Client : " + nomClient);
                contentStream.newLine();
                contentStream.showText("Bien concerné : " + bien);
                contentStream.newLine();
                contentStream.showText("Date : " + LocalDate.now());
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Ce document constitue un contrat officiel entre les deux parties.");
                contentStream.newLine();
                contentStream.showText("Les termes et conditions sont définis selon l'accord établi.");
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Signature du client : ________________________");

                contentStream.endText();
            }

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                document.save(outputStream);
                return outputStream.toByteArray();
            }
        }
    }
}
