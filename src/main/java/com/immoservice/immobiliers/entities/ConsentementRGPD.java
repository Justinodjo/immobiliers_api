package com.immoservice.immobiliers.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "consentements_rgpd")
public class ConsentementRGPD {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo user;

    @Enumerated(EnumType.STRING)
    private TypeConsentement typeConsentement;

    @Enumerated(EnumType.STRING)
    private StatutConsentement statut;

    @Column(name = "date_consentement", nullable = false)
    private Date dateConsentement;

    private Date dateExpiration;

    private String versionPolitique;

    private String ipAddress;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public TypeConsentement getTypeConsentement() {
        return typeConsentement;
    }

    public void setTypeConsentement(TypeConsentement typeConsentement) {
        this.typeConsentement = typeConsentement;
    }

    public StatutConsentement getStatut() {
        return statut;
    }

    public void setStatut(StatutConsentement statut) {
        this.statut = statut;
    }

    public Date getDateConsentement() {
        return dateConsentement;
    }

    public void setDateConsentement(Date dateConsentement) {
        this.dateConsentement = dateConsentement;
    }

    public Date getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Date dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public String getVersionPolitique() {
        return versionPolitique;
    }

    public void setVersionPolitique(String versionPolitique) {
        this.versionPolitique = versionPolitique;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Getters et setters...

}
