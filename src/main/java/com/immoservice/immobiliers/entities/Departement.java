package com.immoservice.immobiliers.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Departement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDepartement;
    private Date version;
    private String libelle;
    private Date datecreation;
    @ManyToOne
    @JoinColumn(name = "pays_id") // nom de la colonne FK dans la table departement
    private Pays pays;

    public Long getIdDepartement() {
        return idDepartement;
    }

    public void setIdDepartement(Long idDepartement) {
        this.idDepartement = idDepartement;
    }

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Date getDatecreation() {
        return datecreation;
    }

    public void setDatecreation(Date datecreation) {
        this.datecreation = datecreation;
    }

    public Pays getPays() {
        return pays;
    }

    public void setPays(Pays pays) {
        this.pays = pays;
    }

}
