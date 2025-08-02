package com.immoservice.immobiliers.entities;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
    private String status; // actif, resilié, expiré
    private Double loyerMensuel;
    private Double prixTotal;
    private Date dateDebut;
    private Date dateFin;
    private Double fraisAnnulation;
    @Enumerated(EnumType.STRING)
    private TypeContrat typeContrat;

    public TypeContrat getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(TypeContrat typeContrat) {
        this.typeContrat = typeContrat;
    }

    @ManyToOne
    @JoinColumn(name = "user_info_id") // Clé étrangère pour lier UserInfo à Reservation
    private UserInfo userInfo;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REMOVE, CascadeType.REFRESH })
    @JoinTable(name = "contrats__biens_immobiliere", joinColumns = @JoinColumn(name = "contrats_id"), inverseJoinColumns = @JoinColumn(name = "biens_immobiliere_id"))
    private List<BiensImmobiliers> biensImmobiliers = new ArrayList<>();

    // Méthode pour calculer le prix total
    public Double calculatePrixTotal() {
        double total = biensImmobiliers.stream()
                .mapToDouble(biensImmo -> {
                    // Si prix est 0 ou non valide, le considérer comme 0
                    int prix = biensImmo.getPrix();
                    // Si le prix est 0, on peut choisir de le traiter comme invalide ou de
                    // l'ignorer
                    return (prix > 0) ? prix : 0.0; // Ignorer les prix invalides
                })
                .sum();

        // Mettre à jour le prixTotal
        this.prixTotal = total;
        System.out.println(">>> Prix total calculé : " + total); // Pour debug
        return total;
    }

    public boolean estEcheanceProchaine(int joursAvantEcheance) {
        Date dateActuelle = new Date();
        long difference = dateFin.getTime() - dateActuelle.getTime();

        long joursRestants = difference / (24 * 60 * 60 * 1000);

        return joursRestants <= joursAvantEcheance;
    }

    public void annulerContrat(Date dateAnnulation) {
        switch (typeContrat) {
            case MEUBLEE:
                fraisAnnulation = calculerFraisAnnulationMeublee(dateAnnulation);
                break;
            case NON_MEUBLEE:
                fraisAnnulation = calculerFraisAnnulationNonMeublee(dateAnnulation);
                break;
            default:
                // throw UnsupportedOperationException("Type de Contrat non supporté");
        }
    }

    private double calculerFraisAnnulationMeublee(Date dateAnnulation) {
        if (dateAnnulation.before(dateDebut)) {
            fraisAnnulation = (double) 0;
        } else if (dateAnnulation.after(dateDebut) && dateAnnulation.before(dateFin)) {
            long differenceAnnulation = dateFin.getTime() - dateAnnulation.getTime();
            long mois = differenceAnnulation / (30 * 24 * 60 * 60 * 1000);
            fraisAnnulation = loyerMensuel * mois * 0.5;
        } else {
            fraisAnnulation = loyerMensuel * 12;
        }
        return fraisAnnulation;
    }

    private double calculerFraisAnnulationNonMeublee(Date dateAnnulation) {
        if (dateAnnulation.before(dateDebut)) {
            fraisAnnulation = (double) 0;
        } else if (dateAnnulation.after(dateDebut) && dateAnnulation.before(dateFin)) {
            long differenceAnnulation = dateFin.getTime() - dateAnnulation.getTime();
            long mois = differenceAnnulation / (30 * 24 * 60 * 60 * 1000);
            fraisAnnulation = loyerMensuel * mois * 0.30;
        } else {
            fraisAnnulation = loyerMensuel * 12;
        }
        return fraisAnnulation;
    }

    public Contrats(Long id, String status, Double prixTotal, Double loyerMensuel, Date dateDebut,
            Date dateFin, UserInfo userInfo) {
        this.id = id;
        this.status = status;
        this.loyerMensuel = loyerMensuel;
        this.prixTotal = prixTotal;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.userInfo = userInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getLoyerMensuel() {
        return loyerMensuel;
    }

    public void setLoyerMensuel(Double loyerMensuel) {
        this.loyerMensuel = loyerMensuel;
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
}
