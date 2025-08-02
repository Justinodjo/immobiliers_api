package com.immoservice.immobiliers.services;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.entities.DocumentVersion;
import com.immoservice.immobiliers.entities.Documents;
import com.immoservice.immobiliers.repository.DocumentVersionRepository;

@Service
public class DocumentVersionService {
    private final DocumentVersionRepository documentVersionRepository;

    public DocumentVersionService(DocumentVersionRepository documentVersionRepository) {
        this.documentVersionRepository = documentVersionRepository;
    }

    public DocumentVersion createDocumentVersion(Documents documents, String contenu) {
        DocumentVersion version = new DocumentVersion();
        version.setContenu(contenu);
        version.setDateCreation(new Date(System.currentTimeMillis()));

        // Vérifie si le document a déjà des versions pour calculer le bon numéro
        List<DocumentVersion> existingVersions = documentVersionRepository.findByDocuments(documents);
        version.setNumeroVersion(existingVersions != null ? existingVersions.size() + 1 : 1);

        version.setDocuments(documents);
        return documentVersionRepository.save(version);
    }

    public List<DocumentVersion> listDocumentVersions(Documents documents) {
        return documentVersionRepository.findByDocuments(documents);
    }

    public DocumentVersion findDocumentVersionById(Long id) {
        return documentVersionRepository.findById(id).orElse(null);
    }
}
