package com.immoservice.immobiliers.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Quartier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idQuartier;
    private Date datecreation;
    private String libelle;
    private Date version;
    @ManyToOne(cascade = { PERSIST, MERGE })
    @JoinColumn(name = "idArrondissement")
    private Arrondissement arrondissement;

    public Long getIdQuartier() {
        return idQuartier;
    }

    public void setIdQuartier(Long idQuartier) {
        this.idQuartier = idQuartier;
    }

    public Date getDatecreation() {
        return datecreation;
    }

    public void setDatecreation(Date datecreation) {
        this.datecreation = datecreation;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }

    public Arrondissement getArrondissement() {
        return arrondissement;
    }

    public void setArrondissement(Arrondissement arrondissement) {
        this.arrondissement = arrondissement;
    }

}
