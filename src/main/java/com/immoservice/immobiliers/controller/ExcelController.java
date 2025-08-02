// package com.project.projetOne.controller;

// import java.io.IOException;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.project.projetOne.service.ExcelService;

// @RestController
// @RequestMapping("api/excel")
// public class ExcelController {

// @Autowired
// private ExcelService excelService;

// @GetMapping("/updateExcel")
// public ResponseEntity<String> updateExcelFile(@RequestParam String filePath,
// @RequestParam("ficheId") Long ficheId) {
// try {
// excelService.updateExcelFile(filePath, ficheId);
// return ResponseEntity.ok("Fichier Excel mis à jour avec succès!");
// } catch (IllegalArgumentException e) {
// return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fiche de suivi
// annuel non trouvée.");
// } catch (IOException e) {
// System.out.println("voici l'erreur :"+e.getMessage());
// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur
// lors de la mise à jour du fichier Excel.");
// }
// }
// }