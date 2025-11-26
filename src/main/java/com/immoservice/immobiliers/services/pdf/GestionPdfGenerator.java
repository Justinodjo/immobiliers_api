package com.immoservice.immobiliers.services.pdf;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

@Component("GESTION")
public class GestionPdfGenerator implements PdfContractGenerator<GestionPdfGenerator.GestionData> {

    @Override
    public byte[] generate(GestionData data) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                cs.newLineAtOffset(50, 750);
                cs.showText("CONTRAT DE GESTION IMMOBILIÈRE");
                cs.newLine();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLine();
                cs.showText("Propriétaire : " + data.nomProprietaire);
                cs.newLine();
                cs.showText("Bien : " + data.adresseBien);
                cs.newLine();
                cs.showText("Date : " + LocalDate.now());
                cs.newLine();
                cs.showText("Conditions : " + data.conditions);
                cs.newLine();
                cs.showText("Signature : ______________________");
                
                cs.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos.toByteArray();
        }
    }

    public static class GestionData {
        public String nomProprietaire;
        public String adresseBien;
        public String conditions;
    }
}
