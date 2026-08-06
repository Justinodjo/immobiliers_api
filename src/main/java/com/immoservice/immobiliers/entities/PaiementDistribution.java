package com.immoservice.immobiliers.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PaiementDistribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Paiement source (mensuel)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paiement_id")
    private Paiements paiement;

    // 🔗 Contrat associé
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id")
    private Contrats contrat;

    // 🔗 Bien principal
    @ManyToOne(fetch = FetchType.LAZY)
    private BiensImmobiliers bien;

    // 🔗 Détail du bien (ex: chambre)
    @ManyToOne(fetch = FetchType.LAZY)
    private DetailsBiensImmobiliers detail;

    // 💰 Montants calculés
    private Double montantBrut;
    private Double commissionSysteme;
    private Double commissionGestionnaire;
    private Double montantNetProprietaire;
    private Double montantNetGestionnaire;

    // 👥 Bénéficiaires
    @ManyToOne(fetch = FetchType.LAZY)
    private UserInfo beneficiaireProprietaire;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserInfo beneficiaireGestionnaire;

    private boolean paye = false;
    private LocalDate dateCreation = LocalDate.now();

    public PaiementDistribution() {
    }

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public Paiements getPaiement() {
        return paiement;
    }

    public void setPaiement(Paiements paiement) {
        this.paiement = paiement;
    }

    public Contrats getContrat() {
        return contrat;
    }

    public void setContrat(Contrats contrat) {
        this.contrat = contrat;
    }

    public BiensImmobiliers getBien() {
        return bien;
    }

    public void setBien(BiensImmobiliers bien) {
        this.bien = bien;
    }

    public DetailsBiensImmobiliers getDetail() {
        return detail;
    }

    public void setDetail(DetailsBiensImmobiliers detail) {
        this.detail = detail;
    }

    public Double getMontantBrut() {
        return montantBrut;
    }

    public void setMontantBrut(Double montantBrut) {
        this.montantBrut = montantBrut;
    }

    public Double getCommissionSysteme() {
        return commissionSysteme;
    }

    public void setCommissionSysteme(Double commissionSysteme) {
        this.commissionSysteme = commissionSysteme;
    }

    public Double getCommissionGestionnaire() {
        return commissionGestionnaire;
    }

    public void setCommissionGestionnaire(Double commissionGestionnaire) {
        this.commissionGestionnaire = commissionGestionnaire;
    }

    public Double getMontantNetProprietaire() {
        return montantNetProprietaire;
    }

    public void setMontantNetProprietaire(Double montantNetProprietaire) {
        this.montantNetProprietaire = montantNetProprietaire;
    }

    public Double getMontantNetGestionnaire() {
        return montantNetGestionnaire;
    }

    public void setMontantNetGestionnaire(Double montantNetGestionnaire) {
        this.montantNetGestionnaire = montantNetGestionnaire;
    }

    public UserInfo getBeneficiaireProprietaire() {
        return beneficiaireProprietaire;
    }

    public void setBeneficiaireProprietaire(UserInfo beneficiaireProprietaire) {
        this.beneficiaireProprietaire = beneficiaireProprietaire;
    }

    public UserInfo getBeneficiaireGestionnaire() {
        return beneficiaireGestionnaire;
    }

    public void setBeneficiaireGestionnaire(UserInfo beneficiaireGestionnaire) {
        this.beneficiaireGestionnaire = beneficiaireGestionnaire;
    }

    public boolean isPaye() {
        return paye;
    }

    public void setPaye(boolean paye) {
        this.paye = paye;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }
}
