package com.immoservice.immobiliers.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.CascadeType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class Offres {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TypeOffre typeOffre;
    private BigDecimal montantReduction;
    private Date dateDebut;
    private Date dateFin;
    private String description;
    // Getters and Setters

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bien_id")
    private BiensImmobiliers bien;
    @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL)
    private List<OffreClient> offreClients;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeOffre getTypeOffre() {
        return typeOffre;
    }

    public void setTypeOffre(TypeOffre typeOffre) {
        this.typeOffre = typeOffre;
    }

    public BigDecimal getMontantReduction() {
        return montantReduction;
    }

    public void setMontantReduction(BigDecimal montantReduction) {
        this.montantReduction = montantReduction;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BiensImmobiliers getBien() {
        return bien;
    }

    public void setBien(BiensImmobiliers bien) {
        this.bien = bien;
    }

    public List<OffreClient> getOffreClients() {
        return offreClients;
    }

    public void setOffreClients(List<OffreClient> offreClients) {
        this.offreClients = offreClients;
    }

}