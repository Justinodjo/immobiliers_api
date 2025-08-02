package com.immoservice.immobiliers.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.immoservice.immobiliers.entities.Fichier;
import com.immoservice.immobiliers.repository.FichierRepository;

@Service
public class FichierService {

    @Autowired
    private FichierRepository fichierRepository;

    @Value("${upload.path}")
    private String pathFile;

    @SuppressWarnings("null")
    public Fichier saveFile(MultipartFile file, String nom) throws IOException {
        // Extraire l'extension du fichier
        String originalFileName = file.getOriginalFilename();
        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // Définir le chemin du fichier
        Path path = Paths.get(pathFile + nom + extension);
        file.transferTo(new File(path.toString()));

        // Créer un objet Fichier et le sauvegarder en base de données
        Fichier fichier = new Fichier();
        fichier.setContentType(file.getContentType());
        fichier.setNom(nom + extension); // Inclure l'extension dans le nom
        fichier.setOriginalName(originalFileName);
        fichier.setTaille(file.getSize());

        return fichierRepository.save(fichier);
    }

    public Optional<Fichier> getFile(Long id) {
        return fichierRepository.findById(id);
    }

    public boolean deleteFile(Long id) throws IOException {
        // Trouver le fichier en base de données
        Optional<Fichier> fichierOptional = fichierRepository.findById(id);

        if (fichierOptional.isPresent()) {
            Fichier fichier = fichierOptional.get();
            String fileName = fichier.getNom(); // Nom du fichier à supprimer
            Path filePath = Paths.get(pathFile + fileName); // Chemin du fichier dans le dossier

            // Supprimer le fichier physique
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                e.printStackTrace();
                return false; // Retourner false si la suppression du fichier échoue
            }

            // Supprimer le fichier de la base de données
            fichierRepository.delete(fichier);
            return true; // Retourner true si tout s'est bien passé
        }

        return false; // Retourner false si le fichier n'existe pas en base
    }
}
