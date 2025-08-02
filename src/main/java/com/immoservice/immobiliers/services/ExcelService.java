
// import org.apache.poi.hssf.usermodel.HSSFWorkbook;
// import org.apache.poi.ss.usermodel.*;
// import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.io.FileInputStream;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.StandardCopyOption;

// @Service
// public class ExcelService {
// @Autowired
// private FicheSuiviAnnuelRepository ficheSuiviAnnuelRepository;

// private static final String DEFAULT_FILE_PATH =
// "src/main/resources/Fiche_de_suivi_annuel.xls";

// public void updateExcelFile(String filePath, Long ficheId) throws IOException
// {
// // Utiliser le chemin par défaut si aucun chemin n'est fourni
// if (filePath == null || filePath.isEmpty()) {
// filePath = DEFAULT_FILE_PATH;
// }

// // Vérifier si le chemin est valide (ajoutez d'autres validations si
// nécessaire)
// if (!filePath.endsWith(".xls") && !filePath.endsWith(".xlsx")) {
// throw new IllegalArgumentException("Le fichier doit être au format .xls ou
// .xlsx");
// }

// // Récupérer la fiche de suivi annuel
// FicheSuiviAnnuel fiche = ficheSuiviAnnuelRepository.findById(ficheId)
// .orElseThrow(() -> new IllegalArgumentException("Fiche de suivi annuel non
// trouvée"));

// // Créer un fichier temporaire
// Path originalFilePath = Path.of(filePath);
// Path tempFilePath = Files.createTempFile("temp_excel_file",
// originalFilePath.toString().endsWith(".xlsx") ? ".xlsx" : ".xls");

// try (FileInputStream fileInputStream = new FileInputStream(filePath);
// Workbook workbook = filePath.endsWith(".xlsx") ? new
// XSSFWorkbook(fileInputStream) : new HSSFWorkbook(fileInputStream)) {

// Sheet sheet = workbook.getSheetAt(0); // On suppose que vous travaillez avec
// la première feuille
// Sheet Sheets = workbook.getSheetAt(1); // Accéder à la seconde feuille
// // Mise à jour des différentes sections
// updateIdentificationSection(sheet, fiche);
// updateResultatsParClasseSection(sheet, fiche);
// updateResultatsDesExamensSection(sheet, fiche);
// updateInfrastructuresEquipementsSection(sheet, fiche);

// // Mise à jour de la seconde feuille
// updatePersonnelsGestionsEncadrement(Sheets, fiche); // Supposons que vous
// avez une méthode pour ça

// try (FileOutputStream fileOutputStream = new
// FileOutputStream(tempFilePath.toFile())) {
// workbook.write(fileOutputStream);
// }
// }

// // Obtenez le chemin du dossier Téléchargements de l'utilisateur
// String userHome = System.getProperty("user.home");
// Path downloadsFolder = Path.of(userHome, "Downloads");

// Path newFilePath = downloadsFolder.resolve(originalFilePath.getFileName());

// // Vérifiez si le fichier existe déjà et modifiez le nom si nécessaire
// int fileIndex = 1;
// String fileName = originalFilePath.getFileName().toString();
// String fileExtension = "";

// // Séparez le nom du fichier de son extension
// int dotIndex = fileName.lastIndexOf('.');
// if (dotIndex != -1) {
// fileExtension = fileName.substring(dotIndex);
// fileName = fileName.substring(0, dotIndex);
// }

// // Renommez le fichier jusqu'à ce qu'un nom unique soit trouvé
// while (Files.exists(newFilePath)) {
// newFilePath = downloadsFolder.resolve(fileName + "_" + fileIndex +
// fileExtension);
// fileIndex++;
// }

// // Déplacez le fichier temporaire vers le nouveau chemin
// try {
// Files.move(tempFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
// System.out.println("Fichier téléchargé : " + newFilePath);
// } catch (Exception e) {
// e.printStackTrace();
// }
// // Copier le fichier temporaire vers le dossier Téléchargements
// // Files.move(tempFilePath, newFilePath,
// StandardCopyOption.REPLACE_EXISTING);
// }

// private void updateIdentificationSection(Sheet sheet, FicheSuiviAnnuel fiche)
// {
// // Mise à jour des cellules
// Row row = sheet.getRow(9); // Adaptez la ligne selon votre fichier Excel
// if (row == null) {
// row = sheet.createRow(9);
// }

// // Exemple pour NOM DE L'ECOLE
// Cell cell = row.getCell(1);
// if (cell == null) {
// cell = row.createCell(1);
// }
// cell.setCellValue(fiche.getEcole().getNomEcole());

// // Exemple pour CODE D'IDENTIFICATION
// Cell codeCell = row.getCell(9);
// if (codeCell == null) {
// codeCell = row.createCell(9);
// }
// codeCell.setCellValue(fiche.getEcole().getCodeIdentification()); // Adaptez
// selon votre modèle

// Row rowdepartement = sheet.getRow(10); // Adaptez la ligne selon votre
// fichier Excel
// if (rowdepartement == null) {
// rowdepartement = sheet.createRow(10);
// }
// // Exemple pour DEPARTEMENT
// Cell departementCell = rowdepartement.getCell(1);
// if (departementCell == null) {
// departementCell = rowdepartement.createCell(1);
// }
// departementCell.setCellValue(fiche.getEcole().getDepartement()); // Adaptez
// selon votre modèle

// // Exemple pour EFFECTIFTOTAL TOTAL DES GARCONS
// Cell effectifTotalGarconsCell = rowdepartement.getCell(9);
// if (effectifTotalGarconsCell == null) {
// effectifTotalGarconsCell = rowdepartement.createCell(9);
// }
// effectifTotalGarconsCell.setCellValue(fiche.getEcole().getEffectifTotalGarcons());
// // Adaptez selon votre modèle

// // Exemple pour EFFECTIFTOTAL TOTAL DES GARCONS
// Cell effectifTotalFillesCell = rowdepartement.getCell(11);
// if (effectifTotalFillesCell == null) {
// effectifTotalFillesCell = rowdepartement.createCell(11);
// }
// effectifTotalFillesCell.setCellValue(fiche.getEcole().getEffectifTotalFilles());
// // Adaptez selon votre modèle

// // Calcul de l'effectif total
// int effectifTotal = fiche.getEcole().getEffectifTotalGarcons() +
// fiche.getEcole().getEffectifTotalFilles();

// // Exemple pour EFFECTIF TOTAL GLOBAL
// Cell effectifTotalGlobalCell = rowdepartement.getCell(6); // Supposons que la
// colonne 13 est pour le total global
// if (effectifTotalGlobalCell == null) {
// effectifTotalGlobalCell = rowdepartement.createCell(6);
// }
// effectifTotalGlobalCell.setCellValue(effectifTotal); // Enregistre l'effectif
// total global

// Row rowCommune = sheet.getRow(12); // Adaptez la ligne selon votre fichier
// Excel
// if (rowCommune == null) {
// rowCommune = sheet.createRow(12);
// }
// // Exemple pour La COMMUNE
// Cell communeCell = rowCommune.getCell(1);
// if (communeCell == null) {
// communeCell = rowCommune.createCell(1);
// }
// communeCell.setCellValue(fiche.getEcole().getCommune()); // Adaptez selon
// votre modèle

// // Exemple pour EFFECTIFTOTAL TOTAL MATERNELLE DES GARCONS
// Cell effectifTotalMaternelleGarconsCell = rowCommune.getCell(9);
// if (effectifTotalMaternelleGarconsCell == null) {
// effectifTotalMaternelleGarconsCell = rowCommune.createCell(9);
// }
// effectifTotalMaternelleGarconsCell.setCellValue(fiche.getEcole().getEffectifMaternelleGarcons());
// // Adaptez selon votre modèle

// // Exemple pour EFFECTIFTOTAL TOTAL MATERNELLE DES GARCONS
// Cell effectifTotalMaternelleFillesCell = rowCommune.getCell(11);
// if (effectifTotalMaternelleFillesCell == null) {
// effectifTotalMaternelleFillesCell = rowCommune.createCell(11);
// }
// effectifTotalMaternelleFillesCell.setCellValue(fiche.getEcole().getEffectifMaternelleFilles());
// // Adaptez selon votre

// Row rowArrondissement = sheet.getRow(14); // Adaptez la ligne selon votre
// fichier Excel
// if (rowArrondissement == null) {
// rowArrondissement = sheet.createRow(14);
// }

// // Exemple pour ARRONDISSEMENT
// Cell arrondissementCell = rowArrondissement.getCell(1);
// if (arrondissementCell == null) {
// arrondissementCell = rowArrondissement.createCell(1);
// }
// arrondissementCell.setCellValue(fiche.getEcole().getArrondissement()); //
// Adaptez selon votre modèle

// // Exemple pour CONTACT
// Cell contactCell = rowArrondissement.getCell(6);
// if (contactCell == null) {
// contactCell = rowArrondissement.createCell(6);
// }
// contactCell.setCellValue(fiche.getEcole().getContact()); // Adaptez selon
// votre modèle

// Row rowAnnee = sheet.getRow(16); // Adaptez la ligne selon votre fichier
// Excel
// if (rowAnnee == null) {
// rowAnnee = sheet.createRow(16);
// }

// // // Exemple pour CONTACT
// // Cell anneeDebutCell = rowAnnee.getCell(1);
// // if (anneeDebutCell == null) {
// // anneeDebutCell = rowAnnee.createCell(1);
// // }
// // anneeDebutCell.setCellValue(fiche.getEcole().getAnneeScolaireDebut()); //
// Adaptez selon votre modèle

// // // Exemple pour CONTACT
// // Cell anneeFinCell = rowAnnee.getCell(3);
// // if (anneeFinCell == null) {
// // anneeFinCell = rowAnnee.createCell(3);
// // }
// // anneeFinCell.setCellValue(fiche.getEcole().getAnneeScolaireFin()); //
// Adaptez selon votre modèle
// }

// private void updateResultatsParClasseSection(Sheet sheet, FicheSuiviAnnuel
// fiche) {
// int startRow = 21; // Adaptez la ligne de départ pour les résultats par
// classe
// int rowNum = startRow;

// for (ResultatsParClasse resultat : fiche.getResultatsParClasse()) {
// Row row = sheet.getRow(rowNum);
// if (row == null) {
// row = sheet.createRow(rowNum);
// }

// row.createCell(0).setCellValue(resultat.getClasse());
// row.createCell(1).setCellValue(resultat.getEffectifInscritGarcons());
// row.createCell(2).setCellValue(resultat.getEffectifInscritFilles());
// row.createCell(3).setCellValue(resultat.getAbandonExclusionGarcons());
// row.createCell(4).setCellValue(resultat.getAbandonExclusionFilles());
// row.createCell(5).setCellValue(resultat.getEffectifFinAnneeGarcons());
// row.createCell(6).setCellValue(resultat.getEffectifFinAnneeFilles());
// row.createCell(9).setCellValue(resultat.getPassageClasseSuperieureGarcons());
// row.createCell(10).setCellValue(resultat.getPassageClasseSuperieurFilles());
// row.createCell(11).setCellValue(resultat.getRedoublementGarcons());
// row.createCell(12).setCellValue(resultat.getRedoublementFilles());

// rowNum++;
// }
// }

// private void updateResultatsDesExamensSection(Sheet sheet, FicheSuiviAnnuel
// fiche) {
// int startRow = 36; // Adaptez la ligne de départ pour les résultats des
// examens
// int rowNum = startRow;

// for (ResultatsDesExamens examen : fiche.getResultatsDesExamens()) {
// Row row = sheet.getRow(rowNum);
// if (row == null) {
// row = sheet.createRow(rowNum);
// }

// row.createCell(0).setCellValue(examen.getNatureExamen());
// row.createCell(1).setCellValue(examen.getInscritsGarcons());
// row.createCell(2).setCellValue(examen.getInscritFilles());
// row.createCell(3).setCellValue(examen.getInscritsGarcons() +
// examen.getInscritFilles());
// row.createCell(4).setCellValue(examen.getPresentsGarcons());
// row.createCell(5).setCellValue(examen.getPresentsFilles());
// row.createCell(6).setCellValue(examen.getPresentsGarcons() +
// examen.getPresentsFilles());
// row.createCell(9).setCellValue(examen.getAdmisGarcons());
// row.createCell(10).setCellValue(examen.getAdmisFilles());
// row.createCell(11).setCellValue(examen.getAdmisGarcons() +
// examen.getAdmisFilles());

// rowNum++;
// }
// }

// private void updateInfrastructuresEquipementsSection(Sheet sheet,
// FicheSuiviAnnuel fiche) {
// int startRow = 41; // Adaptez la ligne de départ pour les infrastructures et
// équipements
// int rowNum = startRow;

// for (Infrastructure infra : fiche.getInfrastructure()) {
// Row row = sheet.getRow(rowNum);
// if (row == null) {
// row = sheet.createRow(rowNum);
// }

// row.createCell(0).setCellValue(infra.getNatureInfrastructure());
// row.createCell(1).setCellValue(infra.getNombreInfrastructure());
// row.createCell(2).setCellValue(infra.isBonEtat() ? "Oui" : "Non");
// row.createCell(3).setCellValue(infra.isMauvaisEtat() ? "Oui" : "Non");

// rowNum++;
// }

// for (Equipement equip : fiche.getEquipement()) {
// Row row = sheet.getRow(rowNum - 2);
// if (row == null) {
// row = sheet.createRow(rowNum - 2);
// }

// row.createCell(5).setCellValue(equip.getNatureEquipement());
// row.createCell(7).setCellValue(equip.getStockInitial());
// row.createCell(8).setCellValue(equip.getEntree());
// row.createCell(9).setCellValue(equip.getStockFinal());
// row.createCell(10).setCellValue(equip.isBonEtat() ? "Oui" : "Non");
// row.createCell(11).setCellValue(equip.isMauvaisEtat() ? "Oui" : "Non");

// rowNum++;
// }
// }

// private Row getOrCreateRow(Sheet sheets, int rowIndex) {
// Row row = sheets.getRow(rowIndex);
// if (row == null) {
// row = sheets.createRow(rowIndex);
// }
// return row;
// }
// private void updatePersonnelsGestionsEncadrement(Sheet sheets,
// FicheSuiviAnnuel fiche) {
// Row row = getOrCreateRow(sheets, 1);
// Row rowComiteGestionCantine = getOrCreateRow(sheets, 2);
// Row rowMembreGestionCantine = getOrCreateRow(sheets, 3);
// Row rowMembrePresidentCantine = getOrCreateRow(sheets, 4);

// // Mise à jour de la cellule pour la présence des cuisinières
// Cell cuisiniereCell = row.getCell(2);
// if (cuisiniereCell == null) {
// cuisiniereCell = row.createCell(2);
// }
// cuisiniereCell.setCellValue(fiche.getPersonnelGestionEncadrement().isCuisinieresPourEcole()
// ? "✔" : "✘");

// sheets.setColumnWidth(2, 4000); // Ajustez la largeur de la colonne 2
// // sheets.setColumnWidth(9, 4000); // Ajustez la largeur de la colonne 9

// // Mise à jour de la cellule pour le comité de gestion de la cantine
// Cell comiteCell = rowComiteGestionCantine.getCell(2);
// if (comiteCell == null) {
// comiteCell = rowComiteGestionCantine.createCell(2);
// }
// boolean cuisinieresExist =
// fiche.getPersonnelGestionEncadrement().isCuisinieresPourEcole();
// comiteCell.setCellValue(cuisinieresExist ? "Oui" : "Non");
// //
// comiteCell.setCellValue(fiche.getPersonnelGestionEncadrement().isComiteGestionCantine()
// ? "✔" : "✘");

// Cell cuisiniereCheckCell = row.getCell(2);
// if (cuisiniereCheckCell == null) {
// cuisiniereCheckCell = row.createCell(2);
// }
// cuisiniereCheckCell.setCellValue(cuisinieresExist ? "✔" : "✘");

// Cell cuisiniereCountCell = row.getCell(3);
// if (cuisiniereCountCell == null) {
// cuisiniereCountCell = row.createCell(3);
// }
// if (cuisinieresExist) {
// int nombreCuisinieres =
// fiche.getPersonnelGestionEncadrement().getNombreCuisinieres(); //
// Assurez-vous que cette méthode existe
// cuisiniereCountCell.setCellValue("Si oui, combien? " + nombreCuisinieres);
// } else {
// cuisiniereCountCell.setCellValue("Si oui, combien? …");
// }

// // Ajoutez d'autres mises à jour ici si nécessaire
// // 5.3 Formation des membres du comité
// Cell formationCell = rowMembreGestionCantine.getCell(2); // Indice de colonne
// pour formation
// if (formationCell == null) {
// formationCell = rowMembreGestionCantine.createCell(2);
// }
// boolean membresFormesCantineExist=
// fiche.getPersonnelGestionEncadrement().isComiteGestionCantine();
// //
// formationCell.setCellValue(fiche.getPersonnelGestionEncadrement().isMembresFormesGestionCantine()
// ? "✔" : "✘");
// formationCell.setCellValue(membresFormesCantineExist ? "Oui" : "Non");

// Cell formationCheckCell = rowMembreGestionCantine.getCell(2);
// if (formationCheckCell == null) {
// formationCheckCell = rowMembreGestionCantine.createCell(2);
// }
// formationCheckCell.setCellValue(membresFormesCantineExist ? "✔" : "✘");

// Cell formationCountCell = rowComiteGestionCantine.getCell(3);
// if (formationCountCell == null) {
// formationCountCell = rowComiteGestionCantine.createCell(3);
// }
// if (membresFormesCantineExist) {
// int nombreMembreComiteGestion =
// fiche.getPersonnelGestionEncadrement().getNombreMembresComite(); //
// Assurez-vous que cette méthode existe
// int
// nombreFemmesComite=fiche.getPersonnelGestionEncadrement().getNombreFemmesComite();
// int
// nombreHommesComite=fiche.getPersonnelGestionEncadrement().getNombreHommesComite();
// formationCountCell.setCellValue("Si oui, combien de membres?" +
// nombreMembreComiteGestion +"Femmes " + " - "+nombreFemmesComite + "Hommes " +
// " - "+ nombreHommesComite);
// } else {
// formationCountCell.setCellValue("Si oui, combien de membres? …….. Femmes
// ….... Hommes……");
// }
// // 5.4 Président femme
// Cell presidentFemmeCell = rowMembrePresidentCantine.getCell(2);
// if (presidentFemmeCell == null) {
// presidentFemmeCell = rowMembrePresidentCantine.createCell(2);
// }
// presidentFemmeCell.setCellValue(fiche.getPersonnelGestionEncadrement().isPresidentComiteFemme()
// ? "✔" : "✘");

// }

// }
