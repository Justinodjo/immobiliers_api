package com.immoservice.immobiliers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.DocumentVersion;
import java.util.List;
import com.immoservice.immobiliers.entities.Documents;
@Repository
public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {
    // Méthode pour trouver les versions d'un document spécifique
    List<DocumentVersion> findByDocuments(Documents documents);
}