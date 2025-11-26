package com.immoservice.immobiliers.services.pdf;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

@Component("MANDAT")
public class MandatPdfGenerator implements PdfContractGenerator<MandatPdfGenerator.MandatData> {

    @Override
    public byte[] generate(MandatData data) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDType0Font font;
            try (InputStream fontIs = getClass().getResourceAsStream("/fonts/DejaVuSans.ttf")) {
                font = PDType0Font.load(doc, fontIs);
            }

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                if (data.logoPath != null) {
                    PDImageXObject logo = PDImageXObject.createFromFile(data.logoPath, doc);
                    cs.drawImage(logo, 50, 740, 80, 40);
                }

                cs.beginText();
                cs.setFont(font, 14);
                cs.setLeading(16);
                cs.newLineAtOffset(50, 700);
                cs.showText("MANDAT ATTRIBUTIF IMMOBILIER");
                cs.newLine();

                cs.setFont(font, 11);
                cs.showText("Syndic : " + data.nomSyndic);
                cs.newLine();
                cs.showText("Adresse : " + data.adresseSyndic);
                cs.newLine();
                cs.showText("Bien : " + data.adresseBien + " (" + data.typeBien + ")");
                cs.newLine();
                cs.showText("Surface : " + data.surface + " m²");
                cs.newLine();
                cs.showText("Période : " + data.dateDebut + " au " + data.dateFin);
                cs.newLine();
                cs.newLine();
                cs.showText("Prix : " + data.prix + " €");
                cs.newLine();
                cs.showText("Honoraires à la charge de : " + data.chargeHonoraires);
                cs.newLine();
                cs.newLine();
                cs.showText("Signature du mandant : " + data.signatureMandant);
                cs.newLine();
                cs.showText("Date : " + LocalDate.now());
                cs.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos.toByteArray();
        }
    }

    public static class MandatData {
        public String logoPath;
        public String nomSyndic;
        public String adresseSyndic;
        public String adresseBien;
        public String typeBien;
        public String surface;
        public String dateDebut;
        public String dateFin;
        public String prix;
        public String chargeHonoraires;
        public String signatureMandant;
    }
}
