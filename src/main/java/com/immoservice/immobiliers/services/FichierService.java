package com.immoservice.immobiliers.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
        Path path = Path.of(pathFile + nom + extension);

        // Enregistrer l'image redimensionnée dans le fichier physique
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

    // public Optional<Fichier> getFileByName(String nom) {
    // return fichierRepository.findByNom(nom);
    // }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean deleteFile(Long id) throws IOException {
        // Trouver le fichier en base de données
        Optional<Fichier> fichierOptional = fichierRepository.findById(id);

        if (fichierOptional.isPresent()) {
            Fichier fichier = fichierOptional.get();
            String fileName = fichier.getNom(); // Nom du fichier à supprimer
            Path filePath = Path.of(pathFile + fileName); // Chemin du fichier dans le dossier

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

    // Méthode pour récupérer un fichier par son nom depuis le système de fichiers
    public Optional<File> getFileByNom(String nom) {
        Optional<Fichier> fichierOptional = fichierRepository.findByNom(nom);
        if (fichierOptional.isPresent()) {
            Fichier fichier = fichierOptional.get();
            String fileName = fichier.getNom();
            Path filePath = Path.of(pathFile, fileName); // Crée le chemin complet du fichier sur le système de fichiers
            File file = filePath.toFile();

            if (file.exists() && file.isFile()) {
                return Optional.of(file);
            }
        }
        return Optional.empty(); // Retourne Optional vide si le fichier n'existe pas ou ne correspond pas
    }
}
