package com.immoservice.immobiliers.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.immoservice.immobiliers.Utility;
import com.immoservice.immobiliers.entities.Fichier;
import com.immoservice.immobiliers.services.FichierService;

import jakarta.servlet.http.HttpServletRequest;

// @CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/fichier")
public class FichierController {
    @Autowired
    private FichierService fichierService;

    @Value("${upload.path}")
    private String pathFile;

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestParam MultipartFile file, HttpServletRequest request) {
        try {
            String nom = "MD-" + Utility.geneRandom(20);
            Fichier saved = fichierService.saveFile(file, nom);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Erreur lors de l'upload du fichier", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> getFile(@PathVariable Long id) {
        Optional<Fichier> fichierData = fichierService.getFile(id);

        if (fichierData.isPresent()) {
            Fichier fichier = fichierData.get();
            try {
                Path filePath = Path.of(pathFile + fichier.getNom());
                Resource resource = new UrlResource(filePath.toUri());
                if (!resource.exists()) {
                    return new ResponseEntity<>("Fichier non trouvé", HttpStatus.NOT_FOUND);
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(fichier.getContentType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + fichier.getOriginalName() + "\"")
                        .body(resource);

            } catch (IOException e) {
                return new ResponseEntity<>("Erreur lors de la lecture du fichier", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("Fichier non trouvé", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFichier(@PathVariable Long id) {
        try {
            boolean success = fichierService.deleteFile(id);
            if (success) {
                return ResponseEntity.ok("Fichier supprimé avec succès.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fichier non trouvé.");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression du fichier.");
        }
    }

    @GetMapping("/getByName/{nom}")
    public ResponseEntity<InputStreamResource> getFileByName(@PathVariable String nom) throws IOException {
        Optional<File> fileOptional = fichierService.getFileByNom(nom);

        if (fileOptional.isPresent()) {
            File file = fileOptional.get();
            FileInputStream fileInputStream = new FileInputStream(file);

            // Récupérer le type MIME du fichier (optionnel)
            String contentType = Files.probeContentType(file.toPath());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .contentType(contentType != null ? MediaType.parseMediaType(contentType)
                            : MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(fileInputStream));
        } else {
            return ResponseEntity.notFound().build(); // Retourne 404 si le fichier n'est pas trouvé
        }
    }
}
