package com.immoservice.immobiliers.entities;

import java.util.Date;

import jakarta.persistence.Entity;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
// @EntityListeners(AuditLogListener.class)
public class Alertes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TypeAlertes typeAlertes;
    private String message;
    private Date dateAlertes;
    private boolean alertesLu;

    @ManyToOne
    @JoinColumn(name = "biens_immobilers_id") // Clé étrangère pour lier UserInfo à Reservation
    private BiensImmobiliers biensImmobilers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeAlertes getTypeAlertes() {
        return typeAlertes;
    }

    public void setTypeAlertes(TypeAlertes typeAlertes) {
        this.typeAlertes = typeAlertes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDateAlertes() {
        return dateAlertes;
    }

    public void setDateAlertes(Date dateAlertes) {
        this.dateAlertes = dateAlertes;
    }

    public boolean isAlertesLu() {
        return alertesLu;
    }

    public void setAlertesLu(boolean alertesLu) {
        this.alertesLu = alertesLu;
    }

    public BiensImmobiliers getBiensImmobilers() {
        return biensImmobilers;
    }

    public void setBiensImmobilers(BiensImmobiliers biensImmobilers) {
        this.biensImmobilers = biensImmobilers;
    }

}
