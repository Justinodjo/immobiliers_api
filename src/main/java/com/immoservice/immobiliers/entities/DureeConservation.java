package com.immoservice.immobiliers.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class DureeConservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ancienConsentementId; // ID de l'ancien enregistrement
    private String tableName;
    @Enumerated(EnumType.STRING)
    private TypeDonnee typeDonnee;
    @Enumerated(EnumType.STRING)
    private BaseLegale baseLegale;
    private int duree;
    private String commentaire;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEnregistrement = new Date();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAncienConsentementId() {
        return ancienConsentementId;
    }

    public void setAncienConsentementId(Long ancienConsentementId) {
        this.ancienConsentementId = ancienConsentementId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public TypeDonnee getTypeDonnee() {
        return typeDonnee;
    }

    public void setTypeDonnee(TypeDonnee typeDonnee) {
        this.typeDonnee = typeDonnee;
    }

    public BaseLegale getBaseLegale() {
        return baseLegale;
    }

    public void setBaseLegale(BaseLegale baseLegale) {
        this.baseLegale = baseLegale;
    }

    public Date getDateEnregistrement() {
        return dateEnregistrement;
    }

    public void setDateEnregistrement(Date dateEnregistrement) {
        this.dateEnregistrement = dateEnregistrement;
    }
}
