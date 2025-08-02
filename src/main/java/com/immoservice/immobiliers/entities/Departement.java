package com.immoservice.immobiliers.entities;

import java.util.Date;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class Departement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDepartement;
    private Date version;
    private String libelle;
    private Date datecreation;
    private Long pays_id;
    
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
    public Long getPays_id() {
        return pays_id;
    }
    public void setPays_id(Long pays_id) {
        this.pays_id = pays_id;
    }

    
    
}
