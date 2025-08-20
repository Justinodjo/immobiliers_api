package com.immoservice.immobiliers.entities;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Contrats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private StatusContrat status; // actif, resilié, expiré
    private Double loyerMensuel;
    private Double prixTotal;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double fraisAnnulation;
    @Enumerated(EnumType.STRING)
    private TypeContrat typeContrat;
    private Double commission;
    @ManyToOne
    @JoinColumn(name = "gestionnaire_id", nullable = true)
    private UserInfo gestionnaire;

    @ManyToOne
    @JoinColumn(name = "user_info_id") // Clé étrangère pour lier UserInfo à Reservation
    private UserInfo userInfo;

    @JsonManagedReference
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "contrats__biens_immobiliere", joinColumns = @JoinColumn(name = "contrats_id"), inverseJoinColumns = @JoinColumn(name = "biens_immobiliere_id"))
    private List<BiensImmobiliers> biensImmobiliers = new ArrayList<>();

    // Méthode pour calculer le prix total
    public Double calculatePrixTotal() {
        double total = biensImmobiliers.stream()
                .mapToDouble(biensImmo -> {

                    // Si le prix est null ou invalide, on le considère comme 0
                    Double prix = biensImmo.getPrix(); // Changer le type de prix pour Double
                    return (prix != null && prix > 0) ? prix : 0.0; // Ignorer les prix invalides
                })
                .sum();

        // Mettre à jour le prixTotal
        this.prixTotal = total;
        System.out.println(">>> Prix total calculé : " + total); // Pour debug
        return total;
    }

    // ✅ Vérifie si échéance est proche
    public boolean estEcheanceProchaine(int joursAvantEcheance) {
        LocalDate dateActuelle = LocalDate.now();
        long joursRestants = ChronoUnit.DAYS.between(dateActuelle, dateFin);
        return joursRestants <= joursAvantEcheance;
    }

    // ✅ Annulation de contrat
    public void annulerContrat(LocalDate dateAnnulation) {
        switch (typeContrat) {
            case MEUBLEE:
                fraisAnnulation = calculerFraisAnnulationMeublee(dateAnnulation);
                break;
            case NON_MEUBLEE:
                fraisAnnulation = calculerFraisAnnulationNonMeublee(dateAnnulation);
                break;
            default:
                fraisAnnulation = 0.0;
        }
        this.status = StatusContrat.RESILIE;
    }

    public TypeContrat getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(TypeContrat typeContrat) {
        this.typeContrat = typeContrat;
    }

    private double calculerFraisAnnulationMeublee(LocalDate dateAnnulation) {
        if (dateAnnulation.isBefore(dateDebut)) {
            return 0.0;
        } else if (dateAnnulation.isAfter(dateDebut) && dateAnnulation.isBefore(dateFin)) {
            long mois = ChronoUnit.MONTHS.between(dateAnnulation, dateFin);
            return loyerMensuel * mois * 0.5;
        } else {
            return loyerMensuel * 12;
        }
    }

    private double calculerFraisAnnulationNonMeublee(LocalDate dateAnnulation) {
        if (dateAnnulation.isBefore(dateDebut)) {
            return 0.0;
        } else if (dateAnnulation.isAfter(dateDebut) && dateAnnulation.isBefore(dateFin)) {
            long mois = ChronoUnit.MONTHS.between(dateAnnulation, dateFin);
            return loyerMensuel * mois * 0.30;
        } else {
            return loyerMensuel * 12;
        }
    }

    public Contrats(Long id,
            StatusContrat status, Double prixTotal, Double loyerMensuel,
            LocalDate dateDebut,
            LocalDate dateFin, UserInfo userInfo) {
        this.id = id;
        this.status = status;
        this.loyerMensuel = loyerMensuel;
        this.prixTotal = prixTotal;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.userInfo = userInfo;
    }

    // ✅ Commission gestionnaire
    public void calculerCommissionGestionnaire(double taux) {
        if (loyerMensuel != null && dateDebut != null && dateFin != null) {
            long dureeMois = ChronoUnit.MONTHS.between(dateDebut, dateFin);
            double totalLoyer = loyerMensuel * dureeMois;
            this.commission = totalLoyer * taux;
        } else {
            this.commission = 0.0;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLoyerMensuel() {
        return loyerMensuel;
    }

    public void setLoyerMensuel(Double loyerMensuel) {
        this.loyerMensuel = loyerMensuel;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(Double prixTotal) {
        this.prixTotal = prixTotal;
    }

    public List<BiensImmobiliers> getBiensImmobiliers() {
        return biensImmobiliers;
    }

    public void setBiensImmobiliers(List<BiensImmobiliers> biensImmobiliers) {
        this.biensImmobiliers = biensImmobiliers;
    }

    public Double getFraisAnnulation() {
        return fraisAnnulation;
    }

    public UserInfo getGestionnaire() {
        return gestionnaire;
    }

    public void setGestionnaire(UserInfo gestionnaire) {
        this.gestionnaire = gestionnaire;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public StatusContrat getStatus() {
        return status;
    }

    public void setStatus(StatusContrat status) {
        this.status = status;
    }
}
