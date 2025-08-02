// package com.project.projetOne.service;

// import org.apache.poi.ss.usermodel.*;
// import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// import org.springframework.stereotype.Service;

// import com.project.projetOne.entity.FicheSuiviAnnuel;
// import com.project.projetOne.dto.FicheSuiviAnnuelDTO; // Assure-toi
// d'importer le bon DTO

// import jakarta.servlet.ServletOutputStream;
// import jakarta.servlet.http.HttpServletResponse;

// import java.io.IOException;
// import java.util.List;
// import java.util.stream.Collectors;

// @Service
// public class ExcelExportService {

// private final FicheSuiviAnnuelService ficheSuiviAnnuelService;

// public ExcelExportService(FicheSuiviAnnuelService ficheSuiviAnnuelService) {
// this.ficheSuiviAnnuelService = ficheSuiviAnnuelService;
// }

// public void exportToExcel(HttpServletResponse response) throws IOException {
// // Récupérer les données à exporter sous forme de DTO
// List<FicheSuiviAnnuelDTO> fichesDTO =
// ficheSuiviAnnuelService.getAllFicheSuiviAnnuels();

// // Créer un workbook et une feuille Excel
// Workbook workbook = new XSSFWorkbook();
// Sheet sheet = workbook.createSheet("Fiches Suivi Annuel");

// // Créer l'entête du fichier Excel
// int rowIdx = 0;
// rowIdx = createHeaders(sheet, rowIdx);

// // Remplir les données
// for (FicheSuiviAnnuelDTO fiche : fichesDTO) {
// rowIdx = fillData(sheet, fiche, rowIdx);
// }

// // Configurer la réponse HTTP pour l'exportation
// response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
// response.setHeader("Content-Disposition", "attachment;
// filename=fiche_suivi_annuel.xlsx");

// // Envoyer le fichier Excel en tant que réponse HTTP
// try (ServletOutputStream outputStream = response.getOutputStream()) {
// workbook.write(outputStream);
// workbook.close();
// }
// }

// private int createHeaders(Sheet sheet, int rowIdx) {
// // En-têtes pour la fiche de suivi annuel
// String[] columns = {
// "ID", "Nom École", "Resultats Examens", "Infrastructure", "Equipement",
// "Cuisinières", "Comité de gestion de cantine", "Formations des membres de
// comité",
// "Président femme", "Bureau APE", "Sensibilisation APE", "Formations APE",
// "Appui d'autres institutions", "Production Jardin", "Production Champ",
// "Production Élevage", "Livraison des vivres", "Appréciation assistance",
// "Autres informations"
// };

// Row headerRow = sheet.createRow(rowIdx++);
// for (int i = 0; i < columns.length; i++) {
// Cell cell = headerRow.createCell(i);
// cell.setCellValue(columns[i]);
// }
// return rowIdx;
// }

// private int fillData(Sheet sheet, FicheSuiviAnnuelDTO fiche, int rowIdx) {
// Row row = sheet.createRow(rowIdx++);
// row.createCell(0).setCellValue(fiche.getIdFicheSuiviAnnuel());
// row.createCell(1).setCellValue(fiche.getEcole() != null ?
// fiche.getEcole().getNomEcole() : "N/A");
// row.createCell(2).setCellValue(fiche.getResultatsDesExamens() != null ?
// fiche.getResultatsDesExamens().toString() : "N/A");
// row.createCell(3).setCellValue(fiche.getInfrastructure() != null ?
// fiche.getInfrastructure().toString() : "N/A");
// row.createCell(4).setCellValue(fiche.getEquipement() != null ?
// fiche.getEquipement().toString() : "N/A");

// // Remplir les autres données
// // Uncomment and modify the following lines based on your DTO fields
// // row.createCell(5).setCellValue(fiche.hasCuisinieres() ? "Oui" : "Non");
// // row.createCell(6).setCellValue(fiche.hasComiteGestionCantine() ? "Oui" :
// "Non");
// // row.createCell(7).setCellValue(fiche.getNombreFormationsComite());
// // row.createCell(8).setCellValue(fiche.isPresidentFemme() ? "Oui" : "Non");
// // row.createCell(9).setCellValue(fiche.hasBureauAPE() ? "Oui" : "Non");
// // row.createCell(10).setCellValue(fiche.getNombreSensibilisationsAPE());
// // row.createCell(11).setCellValue(fiche.getNombreFormationsAPE());
// // row.createCell(12).setCellValue(fiche.getAppuiAutresInstitutions());
// // row.createCell(13).setCellValue(fiche.hasJardin() ? "Oui" : "Non");
// // row.createCell(14).setCellValue(fiche.hasChamp() ? "Oui" : "Non");
// // row.createCell(15).setCellValue(fiche.hasElevage() ? "Oui" : "Non");
// // row.createCell(16).setCellValue(fiche.isLivraisonAEcole() ? "À l'école" :
// "À mi-chemin");
// // row.createCell(17).setCellValue(fiche.getAppreciationAssistance());
// // row.createCell(18).setCellValue(fiche.getAutresInformations());

// return rowIdx;
// }
// }
