package com.immoservice.immobiliers.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.immoservice.immobiliers.config.AuditLogListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@EntityListeners(AuditLogListener.class)
public class Intervention extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String typeIntervention;
    private String description;
    private BigDecimal estimationCout;
    private BigDecimal coutReel;
    private BigDecimal ecartBudgetaire;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date dateSignalement;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTraitement;
    @Enumerated(EnumType.STRING)
    private StatutIntervention statut; // en attente, en cours, terminé
    @Enumerated(EnumType.STRING)
    private NiveauPriorite priorite;

    @ManyToOne
    @JoinColumn(name = "intervention_id")
    private Fichier fichier;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "intervention_fichiers", joinColumns = @JoinColumn(name = "intervention_id"), inverseJoinColumns = @JoinColumn(name = "fichier_id"))
    private List<Fichier> fichiers;

    @ManyToOne
    @JoinColumn(name = "biens_immobilers_id") // Clé étrangère pour lier UserInfo à Reservation
    private BiensImmobiliers biensImmobilers;

    @OneToMany(mappedBy = "intervention", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Documents> documents;

    @CreatedBy
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "created_by_id") // Clé étrangère dans la table produits
    private UserInfo createdBy;

    @CreatedDate
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @LastModifiedBy
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "updated_by_id") // Clé étrangère dans la table produits
    private UserInfo updatedBy;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Fichier> getFichiers() {
        return fichiers;
    }

    public void setFichiers(List<Fichier> fichiers) {
        this.fichiers = fichiers;
    }

    @PrePersist
    @PreUpdate
    public void calculerEcartBudgetaire() {
        if (estimationCout != null && coutReel != null) {
            this.ecartBudgetaire = coutReel.subtract(estimationCout);
        } else {
            this.ecartBudgetaire = BigDecimal.ZERO;
        }
    }

    public String getTypeIntervention() {
        return typeIntervention;
    }

    public void setTypeIntervention(String typeIntervention) {
        this.typeIntervention = typeIntervention;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateSignalement() {
        return dateSignalement;
    }

    public void setDateSignalement(Date dateSignalement) {
        this.dateSignalement = dateSignalement;
    }

    public Date getDateTraitement() {
        return dateTraitement;
    }

    public void setDateTraitement(Date dateTraitement) {
        this.dateTraitement = dateTraitement;
    }

    public StatutIntervention getStatut() {
        return statut;
    }

    public void setStatut(StatutIntervention statut) {
        this.statut = statut;
    }

    public BiensImmobiliers getBiensImmobilers() {
        return biensImmobilers;
    }

    public void setBiensImmobilers(BiensImmobiliers biensImmobilers) {
        this.biensImmobilers = biensImmobilers;
    }

    public BigDecimal getEstimationCout() {
        return estimationCout;
    }

    public void setEstimationCout(BigDecimal estimationCout) {
        this.estimationCout = estimationCout;
    }

    public BigDecimal getCoutReel() {
        return coutReel;
    }

    public void setCoutReel(BigDecimal coutReel) {
        this.coutReel = coutReel;
    }

    public BigDecimal getEcartBudgetaire() {
        return ecartBudgetaire;
    }

    public void setEcartBudgetaire(BigDecimal ecartBudgetaire) {
        this.ecartBudgetaire = ecartBudgetaire;
    }

    public NiveauPriorite getPriorite() {
        return priorite;
    }

    public void setPriorite(NiveauPriorite priorite) {
        this.priorite = priorite;
    }

    public UserInfo getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserInfo createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public UserInfo getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UserInfo updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Documents> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Documents> documents) {
        this.documents = documents;
    }

    public Fichier getFichier() {
        return fichier;
    }

    public void setFichier(Fichier fichier) {
        this.fichier = fichier;
    }

}