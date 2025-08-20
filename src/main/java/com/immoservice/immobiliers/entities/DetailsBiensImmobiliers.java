package com.immoservice.immobiliers.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class DetailsBiensImmobiliers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TypeDetailsBiens typeDetailsBiens;
    private Integer nombre;
    private String nomdetails;
    private String description;
    private Boolean status; // actif, inactif
    private Integer nombreVues = 0;
    private Integer nombreJaime = 0;
    private Double prix;
    @ManyToOne
    private Fichier fichier;
    @OneToMany
    private List<Fichier> fichiers;
    @ManyToOne
    @JoinColumn(name = "biens_immobilers_id")
     @JsonBackReference("biensImmobiliers-detailsBiensImmobiliers")
    private BiensImmobiliers biensImmobilers;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "detailsBiensImmobiliers")
    @JsonManagedReference("detailsBiensImmobiliers-sousDetailsBiensImmobiliers")
    private List<SousDetailsBiensImmobiliers> sousDetailsBiensImmobiliers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BiensImmobiliers getBiensImmobilers() {
        return biensImmobilers;
    }

    public void setBiensImmobilers(BiensImmobiliers biensImmobilers) {
        this.biensImmobilers = biensImmobilers;
    }

    public TypeDetailsBiens getTypeDetailsBiens() {
        return typeDetailsBiens;
    }

    public void setTypeDetailsBiens(TypeDetailsBiens typeDetailsBiens) {
        this.typeDetailsBiens = typeDetailsBiens;
    }

    public Integer getNombre() {
        return nombre;
    }

    public void setNombre(Integer nombre) {
        this.nombre = nombre;
    }

    public Fichier getFichier() {
        return fichier;
    }

    public void setFichier(Fichier fichier) {
        this.fichier = fichier;
    }

    public List<Fichier> getFichiers() {
        return fichiers;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public void setFichiers(List<Fichier> fichiers) {
        this.fichiers = fichiers;
    }

    public List<SousDetailsBiensImmobiliers> getSousDetailsBiensImmobiliers() {
        return sousDetailsBiensImmobiliers;
    }

    public void setSousDetailsBiensImmobiliers(List<SousDetailsBiensImmobiliers> sousDetailsSelectionnes) {
        this.sousDetailsBiensImmobiliers = sousDetailsSelectionnes;
    }

    public String getNomdetails() {
        return nomdetails;
    }

    public void setNomdetails(String nomdetails) {
        this.nomdetails = nomdetails;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getNombreVues() {
        return nombreVues;
    }

    public void setNombreVues(Integer nombreVues) {
        this.nombreVues = nombreVues;
    }

    public Integer getNombreJaime() {
        return nombreJaime;
    }

    public void setNombreJaime(Integer nombreJaime) {
        this.nombreJaime = nombreJaime;
    }

    public void addSousDetails(SousDetailsBiensImmobiliers sousDetail) {
        sousDetailsBiensImmobiliers.add(sousDetail);
        sousDetail.setDetailsBiensImmobiliers(this);
    }

    public void removeSousDetails(SousDetailsBiensImmobiliers sousDetail) {
        sousDetailsBiensImmobiliers.remove(sousDetail);
        sousDetail.setDetailsBiensImmobiliers(null);
    }

}
