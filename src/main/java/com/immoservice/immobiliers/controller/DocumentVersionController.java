package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.entities.DocumentVersion;
import com.immoservice.immobiliers.entities.Documents;
import com.immoservice.immobiliers.repository.DocumentsRepository;
import com.immoservice.immobiliers.services.DocumentVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/document-versions")
@CrossOrigin(origins = "*")
public class DocumentVersionController {

    @Autowired
    private DocumentVersionService documentVersionService;

    @Autowired
    private DocumentsRepository documentsRepository;

    // Créer une nouvelle version pour un document
    @PostMapping("/{documentId}")
    public DocumentVersion createVersion(@PathVariable Long documentId, @RequestBody String contenu) {
        Documents documents = documentsRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable avec l'ID : " + documentId));

        return documentVersionService.createDocumentVersion(documents, contenu);
    }

    // Lister toutes les versions d'un document
    @GetMapping("/by-document/{documentId}")
    public List<DocumentVersion> listVersions(@PathVariable Long documentId) {
        Documents documents = documentsRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable avec l'ID : " + documentId));

        return documentVersionService.listDocumentVersions(documents);
    }

    // Récupérer une version par ID
    @GetMapping("/{id}")
    public DocumentVersion getVersionById(@PathVariable Long id) {
        return documentVersionService.findDocumentVersionById(id);
    }
}
