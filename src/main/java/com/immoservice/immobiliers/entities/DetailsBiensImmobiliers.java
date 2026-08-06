package com.immoservice.immobiliers.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.immoservice.immobiliers.enums.TypeDPE;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
 @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class DetailsBiensImmobiliers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeDetailsBiens typeDetailsBiens;
    @Enumerated(EnumType.STRING)
    private TypeDPE typeDPE;
    private Integer nombre;
    private String nomdetails;
    private String description;
    private Boolean status; // actif, inactif
    @Column(nullable = true)
    private Double note;
    private Integer nombreVues = 0;
    private Integer nombreJaime = 0;
    private Double prix;
    private Double chargeMensuel;
    private Double taxeFonciereAnnuelle;
    private String observations;
    @ManyToOne
    private Fichier fichier;
    
    @OneToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    private List<Fichier> fichiers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "biens_immobiliers_id")
    // @JsonBackReference("biensImmobiliers-detailsBiensImmobiliers")
    private BiensImmobiliers biensImmobiliers;

    @ManyToMany(mappedBy = "detailsBiensImmobiliers", fetch = FetchType.LAZY)
    // @JsonBackReference("contrats-details")
    private List<Contrats> contrats = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "detailsBiensImmobiliers")
    // @JsonManagedReference("detailsBiensImmobiliers-sousDetailsBiensImmobiliers")
    private List<SousDetailsBiensImmobiliers> sousDetailsBiensImmobiliers = new ArrayList<>();

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Contrats> getContrats() {
        return contrats;
    }

    public void setContrats(List<Contrats> contrats) {
        this.contrats = contrats;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BiensImmobiliers getBiensImmobiliers() {
        return biensImmobiliers;
    }

    public void setBiensImmobiliers(BiensImmobiliers biensImmobiliers) {
        this.biensImmobiliers = biensImmobiliers;
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

    public TypeDPE getTypeDPE() {
        return typeDPE;
    }

    public void setTypeDPE(TypeDPE typeDPE) {
        this.typeDPE = typeDPE;
    }

    public Double getChargeMensuel() {
        return chargeMensuel;
    }

    public void setChargeMensuel(Double chargeMensuel) {
        this.chargeMensuel = chargeMensuel;
    }

    public Double getTaxeFonciereAnnuelle() {
        return taxeFonciereAnnuelle;
    }

    public void setTaxeFonciereAnnuelle(Double taxeFonciereAnnuelle) {
        this.taxeFonciereAnnuelle = taxeFonciereAnnuelle;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public void deactivate() {
        this.status = false;
    }

    public Double getNote() {
        return note;
    }

    public void setNote(Double note) {
        this.note = note;
    }

}
