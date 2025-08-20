package com.immoservice.immobiliers.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenu;
    private java.time.LocalDateTime dateEnvoi;

    @ManyToOne
    @JoinColumn(name = "expediteur_id")
    private UserInfo expediteur;

    @ManyToOne
    @JoinColumn(name = "destinataire_id")
    private UserInfo destinataire;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public java.time.LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(java.time.LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public UserInfo getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(UserInfo expediteur) {
        this.expediteur = expediteur;
    }

    public UserInfo getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(UserInfo destinataire) {
        this.destinataire = destinataire;
    }
}
