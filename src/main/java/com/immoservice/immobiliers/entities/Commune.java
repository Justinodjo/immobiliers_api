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
public class Commune {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCommune;
    private Date datecreation;
    
    private String libelle;
    private Date version;
    @ManyToOne
    @JoinColumn(name= "idDepartement")
    private Departement departement;
    
    public Long getIdCommune() {
        return idCommune;
    }
    public void setIdCommune(Long idCommune) {
        this.idCommune = idCommune;
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
    public Departement getDepartement() {
        return departement;
    }
    public void setDepartement(Departement departement) {
        this.departement = departement;
    }
    

    
}
