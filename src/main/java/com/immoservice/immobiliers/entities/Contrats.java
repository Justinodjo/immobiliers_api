package com.immoservice.immobiliers.entities;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.immoservice.immobiliers.enums.ValidationStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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
    @Enumerated(EnumType.STRING)
    private ValidationStatus validationStatus;
    @Column(unique = true, nullable = false)
    private String referenceContrat;
    @Column(name = "avance")
    private Double avance = 0.0;

    private Double commission;
    @Column(name = "commission_systeme")
    private Double commissionSysteme;

    @Column(name = "commission_gestionnaire")
    private Double commissionGestionnaire;

    @ManyToOne
    @JoinColumn(name = "gestionnaire_id", nullable = true)
    private UserInfo gestionnaire;

    @ManyToOne
    @JoinColumn(name = "user_info_id") // Clé étrangère pour lier UserInfo à Reservation
    private UserInfo userInfo;

    // @JsonManagedReference(value = "contrats-biensImmobiliers")
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "contrats__biens_immobiliere", joinColumns = @JoinColumn(name = "contrats_id"), inverseJoinColumns = @JoinColumn(name = "biens_immobiliere_id"))
    private List<BiensImmobiliers> biensImmobiliers = new ArrayList<>();

    // Si contrat sur un ou plusieurs appartements / détails
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "contrats_details_biens", joinColumns = @JoinColumn(name = "contrat_id"), inverseJoinColumns = @JoinColumn(name = "details_bien_id"))
    // @JsonManagedReference("contrats-details")
    private List<DetailsBiensImmobiliers> detailsBiensImmobiliers = new ArrayList<>();

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "contrats_documents", joinColumns = @JoinColumn(name = "contrats_id"), inverseJoinColumns = @JoinColumn(name = "documents_id"))
    private List<Documents> documents = new ArrayList<>();
    @OneToMany(mappedBy = "contrat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaiementDistribution> paiementsDistribues = new ArrayList<>();

    public List<PaiementDistribution> getPaiementsDistribues() {
        return paiementsDistribues;
    }

    public void setPaiementsDistribues(List<PaiementDistribution> paiementsDistribues) {
        this.paiementsDistribues = paiementsDistribues;
    }

    // Méthode pour calculer le prix total des biens principaux
    public Double calculatePrixBiens() {
        double total = biensImmobiliers.stream()
                .mapToDouble(biensImmo -> {
                    // Si le prix est null ou invalide, on le considère comme 0
                    Double prix = biensImmo.getPrix();
                    return (prix != null && prix > 0) ? prix : 0.0;
                })
                .sum();

        System.out.println(">>> Prix total des biens calculé : " + total); // Pour debug
        return total;
    }

    // Méthode pour calculer le prix total des détails
    public Double calculatePrixDetails() {
        double total = detailsBiensImmobiliers.stream()
                .mapToDouble(detail -> {
                    // Si le prix est null ou invalide, on le considère comme 0
                    Double prix = detail.getPrix();
                    return (prix != null && prix > 0) ? prix : 0.0;
                })
                .sum();

        System.out.println(">>> Prix total des détails calculé : " + total); // Pour debug
        return total;
    }

    public boolean estContratValide() {
        boolean aDesBiens = biensImmobiliers != null && !biensImmobiliers.isEmpty();
        boolean aDesDetails = detailsBiensImmobiliers != null && !detailsBiensImmobiliers.isEmpty();

        // Pas les deux à la fois
        return !(aDesBiens && aDesDetails);
    }

    private long getDureeMois() {
        if (dateDebut == null || dateFin == null)
            return 0;
        long mois = ChronoUnit.MONTHS.between(dateDebut, dateFin);
        return Math.max(mois, 1); // éviter 0 ou négatif
    }

    // Méthode globale pour calculer le prix total (biens + détails)

    // ✅ Vérifie si échéance est proche
    public boolean estEcheanceProchaine(int joursAvantEcheance) {
        LocalDate dateActuelle = LocalDate.now();
        long joursRestants = ChronoUnit.DAYS.between(dateActuelle, dateFin);
        return joursRestants <= joursAvantEcheance;
    }

    public void annulerContrat(LocalDate dateAnnulation) {
        // Appeler la méthode de calcul des frais d'annulation en fonction du type de
        // contrat
        fraisAnnulation = switch (typeContrat) {
            case MEUBLEE -> calculerFraisAnnulationMeublee(dateAnnulation);
            case NON_MEUBLEE -> calculerFraisAnnulationNonMeublee(dateAnnulation);
            case SAISONNIERE -> calculerFraisAnnulationSaisonniere(dateAnnulation);
            case COMMERCIALE -> calculerFraisAnnulationCommerciale(dateAnnulation);
            case PROFESSIONNELLE -> calculerFraisAnnulationProfessionnelle(dateAnnulation);
            case ETUDIANT -> calculerFraisAnnulationEtudiant(dateAnnulation);
            case OPTION_ACHAT -> calculerFraisAnnulationOptionAchat(dateAnnulation);
            case ACCESSION -> calculerFraisAnnulationAccession(dateAnnulation);
            default -> 0.0;
        };

        // Changer le statut du contrat en "RESILIE" après l'annulation
        this.status = StatusContrat.RESILIE;
    }

    public TypeContrat getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(TypeContrat typeContrat) {
        this.typeContrat = typeContrat;
    }

    public Double getAvance() {
        return avance;
    }

    public void setAvance(Double avance) {
        this.avance = avance;
    }

    public Double getMontantRestant() {
        double total = (prixTotal != null ? prixTotal : 0.0);
        double totalLoyers = 0.0;

        if (loyerMensuel != null && dateDebut != null && dateFin != null) {
            long dureeMois = ChronoUnit.MONTHS.between(dateDebut, dateFin);
            totalLoyers = loyerMensuel * (dureeMois > 0 ? dureeMois : 1);
        }

        double avancePayee = (avance != null ? avance : 0.0);
        return (total + totalLoyers) - avancePayee;
    }

    @Transient
    public Double getEstimatedTotal() {
        if (loyerMensuel == null || dateDebut == null || dateFin == null) {
            return 0.0;
        }

        return loyerMensuel * getDureeMois();
    }

    // Frais d'annulation pour un contrat saisonnier
    private double calculerFraisAnnulationSaisonniere(LocalDate dateAnnulation) {
        if (dateAnnulation.isBefore(dateDebut)) {
            return 0.0; // Aucun frais avant la date de début
        }
        long joursRestants = ChronoUnit.DAYS.between(dateAnnulation, dateFin); // Jours restants
        return loyerMensuel * (joursRestants / 30.0) * 0.75; // 75% pour chaque jour restant
    }

    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(ValidationStatus validationStatus) {
        this.validationStatus = validationStatus;
    }

    public Double getCommissionGestionnaire() {
        return commissionGestionnaire;
    }

    public void setCommissionGestionnaire(Double commissionGestionnaire) {
        this.commissionGestionnaire = commissionGestionnaire;
    }

    public Double calculatePrixTotal() {
        double total = Stream.concat(
                biensImmobiliers.stream().map(BiensImmobiliers::getPrix),
                detailsBiensImmobiliers.stream().map(DetailsBiensImmobiliers::getPrix))
                .filter(p -> p != null && p > 0)
                .mapToDouble(Double::doubleValue)
                .sum();

        this.prixTotal = total;
        return total;
    }

    // Frais d'annulation pour un contrat commercial
    private double calculerFraisAnnulationCommerciale(LocalDate dateAnnulation) {
        if (dateAnnulation.isBefore(dateDebut)) {
            return 0.0; // Aucun frais avant la date de début
        }
        long moisRestants = ChronoUnit.MONTHS.between(dateAnnulation, dateFin);
        return loyerMensuel * moisRestants * 0.2; // 20% des loyers mensuels restants
    }

    // Frais d'annulation pour un contrat professionnel
    private double calculerFraisAnnulationProfessionnelle(LocalDate dateAnnulation) {
        if (dateAnnulation.isBefore(dateDebut)) {
            return 0.0; // Aucun frais avant la date de début
        }
        long moisRestants = ChronoUnit.MONTHS.between(dateAnnulation, dateFin);
        return loyerMensuel * moisRestants * 0.3; // 30% des loyers mensuels restants
    }

    // Frais d'annulation pour un contrat étudiant
    private double calculerFraisAnnulationEtudiant(LocalDate dateAnnulation) {
        if (dateAnnulation.isBefore(dateDebut)) {
            return 0.0; // Aucun frais avant la date de début
        }
        long moisRestants = ChronoUnit.MONTHS.between(dateAnnulation, dateFin);
        return loyerMensuel * moisRestants * 0.1; // 10% des loyers mensuels restants
    }

    // Frais d'annulation pour un contrat avec option d'achat
    private double calculerFraisAnnulationOptionAchat(LocalDate dateAnnulation) {
        if (dateAnnulation.isBefore(dateDebut)) {
            return 0.0; // Aucun frais avant la date de début
        }
        long moisRestants = ChronoUnit.MONTHS.between(dateAnnulation, dateFin);
        return loyerMensuel * moisRestants * 0.4; // 40% des loyers mensuels restants
    }

    // Frais d'annulation pour un contrat d'accession
    private double calculerFraisAnnulationAccession(LocalDate dateAnnulation) {
        if (dateAnnulation.isBefore(dateDebut)) {
            return 0.0; // Aucun frais avant la date de début
        }
        long moisRestants = ChronoUnit.MONTHS.between(dateAnnulation, dateFin);
        return loyerMensuel * moisRestants * 0.5; // 50% des loyers mensuels restants
    }

    private double calculerFraisAnnulationMeublee(LocalDate dateAnnulation) {
        if (dateAnnulation.isBefore(dateDebut)) {
            return 0.0; // Aucune annulation avant le début
        } else if (dateAnnulation.isAfter(dateDebut) && dateAnnulation.isBefore(dateFin)) {
            long mois = ChronoUnit.MONTHS.between(dateAnnulation, dateFin);
            return loyerMensuel * mois * 0.5; // 50% des loyers mensuels restants
        } else {
            return loyerMensuel * 12; // Si après la fin, on applique une pénalité égale à 12 mois de loyer
        }
    }

    private double calculerFraisAnnulationNonMeublee(LocalDate dateAnnulation) {
        if (dateAnnulation.isBefore(dateDebut)) {
            return 0.0; // Aucune annulation avant le début
        } else if (dateAnnulation.isAfter(dateDebut) && dateAnnulation.isBefore(dateFin)) {
            long mois = ChronoUnit.MONTHS.between(dateAnnulation, dateFin);
            return loyerMensuel * mois * 0.30; // 30% des loyers mensuels restants
        } else {
            return loyerMensuel * 12; // Si après la fin, on applique une pénalité égale à 12 mois de loyer
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

    public Contrats() {
        this.validationStatus = ValidationStatus.EN_ATTENTE;
    }

    // Validation par le gestionnaire
    public void confirmerParGestionnaire() {
        if (this.validationStatus == ValidationStatus.EN_ATTENTE) {
            this.validationStatus = ValidationStatus.CONFIRME_PAR_GESTIONNAIRE;
        }
    }

    // Validation par le propriétaire
    public void validerParProprietaire() {
        if (this.validationStatus == ValidationStatus.CONFIRME_PAR_GESTIONNAIRE) {
            this.validationStatus = ValidationStatus.VALIDEE_PAR_PROPRIETAIRE;
        }
    }

    // Validation par le locataire
    public void validerParLocataire() {
        if (this.validationStatus == ValidationStatus.VALIDEE_PAR_PROPRIETAIRE) {
            this.validationStatus = ValidationStatus.VALIDEE_PAR_LOCATAIRE;
        }
    }

    // Validation par administrateur
    public void confirmerParAdministrateur() {
        this.validationStatus = ValidationStatus.CONFIRME_PAR_ADMINISTRATEUR;
    }

    // Validation par master administrateur
    public void confirmerParMasterAdministrateur() {
        this.validationStatus = ValidationStatus.CONFIRME_PAR_MASTER_ADMINISTRATEUR;
    }

    // Vérifier si paiement autorisé
    public boolean peutPayer() {
        return this.validationStatus == ValidationStatus.VALIDEE_PAR_LOCATAIRE;
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

    public PaiementDistribution genererDistribution(Paiements paiement) {
        PaiementDistribution distribution = new PaiementDistribution();
        distribution.setPaiement(paiement);
        distribution.setContrat(this);

        double montantBrut = paiement.getMontant();
        double commissionSys = this.commissionSysteme != null ? this.commissionSysteme : 0.0;
        double commissionGest = this.commissionGestionnaire != null ? this.commissionGestionnaire : 0.0;

        double montantNetProprietaire = montantBrut - (commissionSys + commissionGest);
        double montantNetGestionnaire = commissionGest;

        distribution.setMontantBrut(montantBrut);
        distribution.setCommissionSysteme(commissionSys);
        distribution.setCommissionGestionnaire(commissionGest);
        distribution.setMontantNetProprietaire(montantNetProprietaire);
        distribution.setMontantNetGestionnaire(montantNetGestionnaire);

        distribution.setBeneficiaireProprietaire(this.userInfo);
        distribution.setBeneficiaireGestionnaire(this.gestionnaire);

        this.paiementsDistribues.add(distribution);
        paiement.getDistributions().add(distribution);

        return distribution;
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

    public List<Documents> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Documents> documents) {
        this.documents = documents;
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

    public List<DetailsBiensImmobiliers> getDetailsBiensImmobiliers() {
        return detailsBiensImmobiliers;
    }

    public void setDetailsBiensImmobiliers(List<DetailsBiensImmobiliers> detailsBiensImmobiliers) {
        this.detailsBiensImmobiliers = detailsBiensImmobiliers;
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

    // Méthode setter (vérifier si elle est bien définie)
    public void setFraisAnnulation(Double fraisAnnulation) {
        this.fraisAnnulation = fraisAnnulation;
    }

    public Double getCommissionSysteme() {
        return commissionSysteme;
    }

    public void setCommissionSysteme(Double commissionSysteme) {
        this.commissionSysteme = commissionSysteme;
    }

    public boolean tousDocumentsFournis() {
        if (documents == null || documents.isEmpty()) {
            return false; // Aucun document associé
        }

        // Vérifie que chaque document a au moins un fichier
        return documents.stream().allMatch(
                doc -> (doc.getFichier() != null) || (doc.getFichiers() != null && !doc.getFichiers().isEmpty()));
    }

    public String getReferenceContrat() {
        return referenceContrat;
    }

    public void setReferenceContrat(String referenceContrat) {
        this.referenceContrat = referenceContrat;
    }

}
