package com.immoservice.immobiliers.dto;

import java.time.LocalDateTime;

public class EvenementHistorique {
    private Long id;
    private String type; // entree, sortie, paiement, incident, travaux, visite
    private String titre;
    private String description;
    private LocalDateTime date;
    private Long contratId;
    private Long locataireId;
    private String locataire;
    private Long bienId;
    private String bien;
    private Double montant;
    private String statut;
    private String notes;
    private Boolean hasDocument = false;

    public EvenementHistorique() {
    }

    public EvenementHistorique(String type, String titre, LocalDateTime date, String statut) {
        this.type = type;
        this.titre = titre;
        this.date = date;
        this.statut = statut;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getContratId() {
        return contratId;
    }

    public void setContratId(Long contratId) {
        this.contratId = contratId;
    }

    public Long getLocataireId() {
        return locataireId;
    }

    public void setLocataireId(Long locataireId) {
        this.locataireId = locataireId;
    }

    public String getLocataire() {
        return locataire;
    }

    public void setLocataire(String locataire) {
        this.locataire = locataire;
    }

    public Long getBienId() {
        return bienId;
    }

    public void setBienId(Long bienId) {
        this.bienId = bienId;
    }

    public String getBien() {
        return bien;
    }

    public void setBien(String bien) {
        this.bien = bien;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getHasDocument() {
        return hasDocument;
    }

    public void setHasDocument(Boolean hasDocument) {
        this.hasDocument = hasDocument;
    }
}
