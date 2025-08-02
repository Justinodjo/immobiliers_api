package com.immoservice.immobiliers.services;

import com.immoservice.immobiliers.entities.Documents;
import com.immoservice.immobiliers.repository.DocumentsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentsService {

    private final DocumentsRepository documentsRepository;

    public DocumentsService(DocumentsRepository documentsRepository) {
        this.documentsRepository = documentsRepository;
    }

    public List<Documents> getAllDocuments() {
        return documentsRepository.findAll();
    }

    public Optional<Documents> getDocumentById(Long id) {
        return documentsRepository.findById(id);
    }

    public Documents createDocument(Documents documents) {
        return documentsRepository.save(documents);
    }

    public Documents updateDocument(Long id, Documents updatedDocument) {
        return documentsRepository.findById(id).map(existingDoc -> {
            existingDoc.setContrats(updatedDocument.getContrats());
            existingDoc.setTypeDocument(updatedDocument.getTypeDocument());
            existingDoc.setFichier(updatedDocument.getFichier());
            existingDoc.setFichiers(updatedDocument.getFichiers());
            existingDoc.setSignature(updatedDocument.getSignature());
            existingDoc.setDocumentVersion(updatedDocument.getDocumentVersion());
            return documentsRepository.save(existingDoc);
        }).orElseThrow(() -> new RuntimeException("Document non trouvé avec l'ID " + id));
    }

    public void deleteDocument(Long id) {
        documentsRepository.deleteById(id);
    }
}
