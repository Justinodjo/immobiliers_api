package com.immoservice.immobiliers.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private String description;

    @ManyToOne
    private Fichier fichier;
    @OneToMany
    private List<Fichier> fichiers;
    @ManyToOne
    @JoinColumn(name = "biens_immobilers_id")
    private BiensImmobiliers biensImmobilers;
    @ManyToOne
    @JoinColumn(name = "sous_details_biens_immobiliers_id")
    private SousDetailsBiensImmobiliers sousDetailsBiensImmobiliers;

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

    public void setFichiers(List<Fichier> fichiers) {
        this.fichiers = fichiers;
    }

    public SousDetailsBiensImmobiliers getSousDetailsBiensImmobiliers() {
        return sousDetailsBiensImmobiliers;
    }

    public void setSousDetailsBiensImmobiliers(SousDetailsBiensImmobiliers sousDetailsBiensImmobiliers) {
        this.sousDetailsBiensImmobiliers = sousDetailsBiensImmobiliers;
    }

}
