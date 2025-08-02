package com.immoservice.immobiliers.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class OffreClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "offre_id")
    private Offres offre;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private UserInfo client;

    @Temporal(TemporalType.DATE)
    private Date dateAttribution;

    private boolean utilisee; // Si le client a utilisé l'offre

    // Optionnel : un champ statut (ex : "active", "expirée", etc.)
    @Enumerated(EnumType.STRING)
    private StatutOffre statut;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Offres getOffre() {
        return offre;
    }

    public void setOffre(Offres offre) {
        this.offre = offre;
    }

    public UserInfo getClient() {
        return client;
    }

    public void setClient(UserInfo client) {
        this.client = client;
    }

    public Date getDateAttribution() {
        return dateAttribution;
    }

    public void setDateAttribution(Date dateAttribution) {
        this.dateAttribution = dateAttribution;
    }

    public boolean isUtilisee() {
        return utilisee;
    }

    public void setUtilisee(boolean utilisee) {
        this.utilisee = utilisee;
    }

    public StatutOffre getStatut() {
        return statut;
    }

    public void setStatut(StatutOffre statut) {
        this.statut = statut;
    }

    // Getters et Setters
}
