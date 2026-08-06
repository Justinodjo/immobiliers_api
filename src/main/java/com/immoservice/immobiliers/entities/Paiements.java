package com.immoservice.immobiliers.entities;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Paiements {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int mois;
    private int annee;
    private Double montant;
    @Column(name = "montant_paye")
    private Double montantPaye;
    // 📅 Nouvelles dates (LocalDate)
    @Column(name = "date_echeance")
    private LocalDate dateEcheance;
    private String reference;
    private Date datePaiement;
    private String typePaiement;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiement statut;
    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement")
    private ModePaiement modePaiement;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiaire_id")
    private UserInfo beneficiaire;
    // 🔹 Optionnel : celui qui effectue le paiement (ex. locataire payant le
    // propriétaire)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payeur_id")
    private UserInfo payeur;
    @Column(name = "date_dernier_rappel")
    private LocalDateTime dateDernierRappel;
    // 🔹 Identifiant de la transaction FedaPay
    @Column(name = "transaction_fedapay_id", unique = true)
    private String transactionFedapayId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrats_id") // Clé étrangère pour lier Contrats à Reservation
    private Contrats contrats;
    @OneToMany(mappedBy = "paiement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaiementDistribution> distributions = new ArrayList<>();

    public List<PaiementDistribution> getDistributions() {
        return distributions;
    }

    public void setDistributions(List<PaiementDistribution> distributions) {
        this.distributions = distributions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ModePaiement getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(ModePaiement modePaiement) {
        this.modePaiement = modePaiement;
    }

    public int getMois() {
        return mois;
    }

    public void setMois(int mois) {
        this.mois = mois;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    // Getter
    public String getReference() {
        return reference;
    }

    // Setter
    public void setReference(String reference) {
        this.reference = reference;
    }

    public Double getMontantPaye() {
        return montantPaye;
    }

    public void setMontantPaye(Double montantPaye) {
        this.montantPaye = montantPaye;
    }

    public LocalDate getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public Date getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(Date datePaiement) {
        this.datePaiement = datePaiement;
    }

    public StatutPaiement getStatut() {
        return statut;
    }

    public void setStatut(StatutPaiement statut) {
        this.statut = statut;
    }

    public UserInfo getBeneficiaire() {
        return beneficiaire;
    }

    public void setBeneficiaire(UserInfo beneficiaire) {
        this.beneficiaire = beneficiaire;
    }

    public String getTypePaiement() {
        return typePaiement;
    }

    public void setTypePaiement(String typePaiement) {
        this.typePaiement = typePaiement;
    }

    public String getTransactionFedapayId() {
        return transactionFedapayId;
    }

    public void setTransactionFedapayId(String transactionFedapayId) {
        this.transactionFedapayId = transactionFedapayId;
    }

    @Transient
    public boolean estEnRetard() {
        return dateEcheance != null &&
                dateEcheance.isBefore(LocalDate.now()) &&
                statut != StatutPaiement.PAYE;
    }

    @Transient
    public int joursRetard() {
        if (!estEnRetard())
            return 0;
        return (int) java.time.temporal.ChronoUnit.DAYS.between(dateEcheance, LocalDate.now());
    }

    @Transient
    public Double getMontantRestant() {
        if (montant == null)
            return 0.0;
        if (montantPaye == null)
            return montant;
        return Math.max(0, montant - montantPaye);
    }

    @Transient
    public boolean estPartiel() {
        return montantPaye != null && montantPaye > 0 && montantPaye < montant;
    }

    // Compatibilité avec l'ancien code
    @Transient
    public Contrats getContrats() {
        return contrats;
    }

    public void setContrats(Contrats contrats) {
        this.contrats = contrats;
    }

    public LocalDateTime getDateDernierRappel() {
        return dateDernierRappel;
    }

    public UserInfo getPayeur() {
        return payeur;
    }

    public void setPayeur(UserInfo payeur) {
        this.payeur = payeur;
    }

    public void setDateDernierRappel(LocalDateTime dateDernierRappel) {
        this.dateDernierRappel = dateDernierRappel;
    }

    // --- Aide : vérifier les rôles du bénéficiaire ---
    @Transient
    public boolean beneficiaireARole(String roleName) {
        return beneficiaire != null && beneficiaire.getRoles().stream()
                .anyMatch(r -> r.getNomRole().equalsIgnoreCase(roleName));
    }

    @Transient
    public boolean payeurARole(String roleName) {
        return payeur != null && payeur.getRoles().stream()
                .anyMatch(r -> r.getNomRole().equalsIgnoreCase(roleName));
    }
}
