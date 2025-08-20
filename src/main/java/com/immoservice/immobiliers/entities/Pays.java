package com.immoservice.immobiliers.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Pays {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate version;
    private String libelle;
    private LocalDate dateCreation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getVersion() {
        return version;
    }

    public void setVersion(LocalDate version) {
        this.version = version;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    // Constructeurs, Getters & Setters

}
