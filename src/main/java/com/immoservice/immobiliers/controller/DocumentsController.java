package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.entities.Documents;
import com.immoservice.immobiliers.services.DocumentsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentsController {

    private final DocumentsService documentsService;

    public DocumentsController(DocumentsService documentsService) {
        this.documentsService = documentsService;
    }

    @GetMapping
    public ResponseEntity<List<Documents>> getAllDocuments() {
        return ResponseEntity.ok(documentsService.getAllDocuments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Documents> getDocumentById(@PathVariable Long id) {
        return documentsService.getDocumentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Documents> createDocument(@RequestBody Documents documents) {
        return ResponseEntity.ok(documentsService.createDocument(documents));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Documents> updateDocument(@PathVariable Long id, @RequestBody Documents updatedDocument) {
        return ResponseEntity.ok(documentsService.updateDocument(id, updatedDocument));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentsService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
