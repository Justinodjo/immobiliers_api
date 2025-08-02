package com.immoservice.immobiliers.entities;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Documents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "contrats_id") // Clé étrangère pour lier Contrats à Reservation
    private Contrats contrats;
    @Enumerated(EnumType.STRING)
    private TypeDocument typeDocument; // etat des lieux, quittance, etc
    private String signature;
    @ManyToOne
    private Fichier fichier;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fichier> fichiers;

    @OneToMany(mappedBy = "documents", cascade = CascadeType.ALL)
    private List<DocumentVersion> documentVersion;

    @ManyToOne
    @JoinColumn(name = "intervention_id")
    private Intervention intervention;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrats getContrats() {
        return contrats;
    }

    public void setContrats(Contrats contrats) {
        this.contrats = contrats;
    }

    public TypeDocument getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(TypeDocument typeDocument) {
        this.typeDocument = typeDocument;
    }

    public Fichier getFichier() {
        return fichier;
    }

    public void setFichier(Fichier fichier) {
        this.fichier = fichier;
    }

    public List<Fichier> getFichiers() {
        return fichiers;
    }

    public void setFichiers(List<Fichier> fichiers) {
        this.fichiers = fichiers;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public List<DocumentVersion> getDocumentVersion() {
        return documentVersion;
    }

    public void setDocumentVersion(List<DocumentVersion> documentVersion) {
        this.documentVersion = documentVersion;
    }

    public Intervention getIntervention() {
        return intervention;
    }

    public void setIntervention(Intervention intervention) {
        this.intervention = intervention;
    }

}