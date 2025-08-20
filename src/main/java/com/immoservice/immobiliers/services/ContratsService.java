package com.immoservice.immobiliers.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.immoservice.immobiliers.entities.BiensImmobiliers;
import com.immoservice.immobiliers.entities.Contrats;
import com.immoservice.immobiliers.entities.StatusContrat;
import com.immoservice.immobiliers.entities.TypeContrat;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.BiensImmobiliersRepository;
import com.immoservice.immobiliers.repository.ContratsRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ContratsService {

    private static final Logger logger = LoggerFactory.getLogger(ContratsService.class);
    @Autowired
    private BiensImmobiliersRepository biensImmobiliersRepository;

    @Autowired
    private ContratsRepository contratsRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Transactional
    public Contrats saveContrat(Contrats contrats, String emailUtilisateur) {
        // Log des informations reçues avant l'enregistrement
        logger.info(">>> Données reçues : {}", contrats);
        logger.debug(">>> Biens associés : {}", contrats.getBiensImmobiliers());
        // Récupération de l'utilisateur connecté
        UserInfo utilisateur = null;
        if (contrats.getUserInfo() != null && contrats.getUserInfo().getId() != null) {
            utilisateur = userInfoRepository.findById(contrats.getUserInfo().getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Utilisateur non trouvé avec l'ID : " + contrats.getUserInfo().getId()));
        } else if (emailUtilisateur != null) {
            utilisateur = userInfoRepository.findByEmail(emailUtilisateur)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Utilisateur non trouvé avec l'email : " + emailUtilisateur));
        } else {
            throw new IllegalArgumentException("Aucun utilisateur associé au contrat.");
        }

        // Associer l'utilisateur à la réservation
        contrats.setUserInfo(utilisateur);
        // Vérifie et associe les services à la réservation
        fetchAndSetBiensImmobiliers(contrats);
        // Calcul du prix total
        contrats.setPrixTotal(contrats.calculatePrixTotal());
        if (contrats.getGestionnaire() != null) {
            contrats.calculerCommissionGestionnaire(0.07); // Appliquer le taux
        }
        // Sauvegarder la réservation
        Contrats savedContrat = contratsRepository.save(contrats);

        // Mise à jour de la note dynamique du bien associé
        BiensImmobiliers bien = contrats.getBiensImmobiliers().get(0); // ou fetch via bienId si besoin

        int nombreContrats = contratsRepository.countByBiensImmobiliers_Id(bien.getId()); // créez cette méthode dans le
                                                                                          // repo

        // Exemple de note : 2 pts par contrat jusqu’à 5 max
        double note = Math.min(nombreContrats * 2.0, 5.0);

        bien.setNote(note);
        biensImmobiliersRepository.save(bien);
        // Log après l'enregistrement
        System.out.println(">>> contrat après enregistrement : " + savedContrat);
        System.out.println(">>> Biens immobiliers après enregistrement : " + savedContrat.getBiensImmobiliers());

        // Retourner la réservation enregistrée
        return savedContrat;
    }

    private void fetchAndSetBiensImmobiliers(Contrats contrats) {
        // Vérifier si les services sont fournis
        if (contrats.getBiensImmobiliers() == null || contrats.getBiensImmobiliers().isEmpty()) {
            throw new IllegalArgumentException("Aucun bien fourni.");
        }

        // Récupérer les IDs des services
        List<Long> biensImmobiliersIds = contrats.getBiensImmobiliers().stream()
                .map(BiensImmobiliers::getId)
                .collect(Collectors.toList());

        // Chercher les services correspondants
        List<BiensImmobiliers> biens = biensImmobiliersRepository.findAllById(biensImmobiliersIds);

        // Vérifier si tous les services sont trouvés
        if (biens.size() != biensImmobiliersIds.size()) {
            throw new EntityNotFoundException("Un ou plusieurs Biens immobiliers fournis ne sont pas valides.");
        }

        // Log des services récupérés
        System.out.println(">>> Biens validés pour le contrat : " + biens);

        // Les services sont déjà valides, on ne change rien dans la réservation
        contrats.setBiensImmobiliers(biens);
    }

    public void traiterContrat(Contrats contrat) {
        if (contrat.getGestionnaire() != null) {
            contrat.calculerCommissionGestionnaire(0.07);
            // Ajoute au solde du gestionnaire, génère une facture, etc.
        } else {
            contrat.setCommission(0.0); // Pas de gestionnaire = pas de commission
        }

        contratsRepository.save(contrat);
    }

    public void assignerGestionnaireEtCalculerCommission(Contrats contrats, UserInfo gestionnaire) {
        contrats.setGestionnaire(gestionnaire);
        contrats.calculerCommissionGestionnaire(0.07); // 7% de commission
        contratsRepository.save(contrats);
    }

    public int calculerScoreUtilisateur(UserInfo user) {
        int score = 0;
        List<Contrats> contrats = contratsRepository.findByUserInfo(user);

        for (Contrats contrat : contrats) {
            TypeContrat type = contrat.getTypeContrat();
            StatusContrat status = contrat.getStatus();

            switch (type) {
                case MEUBLEE:
                    score += 10;
                    break;
                case NON_MEUBLEE:
                    score += 8;
                    break;
                case SAISONNIERE:
                    score += 5;
                    break;
                case COMMERCIALE:
                    score += 12;
                    break;
                case PROFESSIONNELLE:
                    score += 15;
                    break;
                case ETUDIANT:
                    score += 6;
                    break;
                case OPTION_ACHAT:
                    score += 18;
                    break;
                case ACCESSION:
                    score += 20;
                    break;
                default:
                    break;
            }

            // Bonus si contrat actif
            if (status == StatusContrat.ACTIF) {
                score += 5;
            }

            // Bonus sur la durée (si > 6 mois)
            if (contrat.getDateDebut() != null && contrat.getDateFin() != null) {
                long duree = ChronoUnit.DAYS.between(contrat.getDateDebut(), contrat.getDateFin());
                if (duree > 180) {
                    score += 5;
                }
            }

            // Bonus sur le montant du contrat
            if (contrat.getPrixTotal() != null) {
                if (contrat.getPrixTotal() > 100_000) {
                    score += 10;
                } else if (contrat.getPrixTotal() > 50_000) {
                    score += 5;
                }
            }
        }

        return score;
    }

    public double calculerRevenuGestionnaire(UserInfo gestionnaire) {
        return contratsRepository.findByGestionnaire(gestionnaire).stream()
                .mapToDouble(c -> c.getCommission() != null ? c.getCommission() : 0.0)
                .sum();
    }

    public byte[] genererContratPDF(Contrats contrat, String logoPath) {
        try {
            String nomClient = contrat.getUserInfo().getName();
            String typeContrat = contrat.getTypeContrat().name();
            String bien = contrat.getBiensImmobiliers().isEmpty() ? "Non défini"
                    : contrat.getBiensImmobiliers().get(0).getNom(); // ou une description

            return pdfGeneratorService.generateContratAsBytes(nomClient, typeContrat, bien, logoPath);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du contrat PDF", e);
        }
    }

    public List<Contrats> findByUserInfoEmail(String email) {
        return contratsRepository.findByUserInfoEmail(email);
    }

    @Transactional
    public void archiverContratsExpirés() {
        LocalDate aujourdHui = LocalDate.now();

        // Récupère uniquement les contrats expirés mais pas encore archivés
        List<Contrats> contratsExpirés = contratsRepository
                .findByDateFinBeforeAndStatusNot(aujourdHui, StatusContrat.ACTIF); // Remplacez "StatusContrat.ACTIF" si
                                                                                   // vous avez un enum

        for (Contrats contrat : contratsExpirés) {
            contrat.setStatus(StatusContrat.EXPIRE); // Utilisez l'enum plutôt que String
        }

        // Sauvegarde en batch
        contratsRepository.saveAll(contratsExpirés);
    }

    public List<Contrats> rechercherParType(TypeContrat typeContrat) {
        return contratsRepository.findByTypeContrat(typeContrat);
    }

    public List<Contrats> rechercherParStatus(String status) {
        return contratsRepository.findByStatus(status);
    }

    @Transactional
    public Contrats renouvelerContrat(Long idContrat, LocalDate nouvelleDateFin) {
        Contrats contrat = contratsRepository.findById(idContrat)
                .orElseThrow(() -> new EntityNotFoundException("Contrat introuvable"));

        LocalDate aujourdHui = LocalDate.now();

        // Vérification : la nouvelle date doit être après aujourd'hui
        if (nouvelleDateFin.isBefore(aujourdHui)) {
            throw new IllegalArgumentException("La nouvelle date de fin doit être postérieure à aujourd'hui.");
        }

        // Mise à jour des informations du contrat
        contrat.setDateDebut(aujourdHui);
        contrat.setDateFin(nouvelleDateFin);
        contrat.setStatus(StatusContrat.ACTIF);

        return contratsRepository.save(contrat);
    }

    public double calculerRevenuParUtilisateur(UserInfo user) {
        return contratsRepository.findByUserInfo(user).stream()
                .mapToDouble(c -> c.getPrixTotal() != null ? c.getPrixTotal() : 0.0)
                .sum();
    }

    // Export CSV
    public String exportContratsAsCSV() {
        List<Contrats> contrats = contratsRepository.findAll();
        StringBuilder csv = new StringBuilder("ID,Type,Statut,Client,Bien,Début,Fin,Prix\n");

        for (Contrats contrat : contrats) {
            String client = contrat.getUserInfo() != null ? contrat.getUserInfo().getName() : "Inconnu";
            String bien = contrat.getBiensImmobiliers().isEmpty() ? "Aucun"
                    : contrat.getBiensImmobiliers().get(0).getNom();
            csv.append("%d,%s,%s,%s,%s,%s,%s,%.2f\n".formatted(
                    contrat.getId(),
                    contrat.getTypeContrat(),
                    contrat.getStatus().name(), // Enum au lieu de String
                    client,
                    bien,
                    contrat.getDateDebut(),
                    contrat.getDateFin(),
                    contrat.getPrixTotal() != null ? contrat.getPrixTotal() : 0.0));
        }

        return csv.toString();
    }

    // Recherche contrats
    public List<Contrats> rechercherContrats(String type, StatusContrat status, LocalDate dateDebutMin,
            LocalDate dateFinMax) {
        return contratsRepository.findAll().stream()
                .filter(c -> (type == null || c.getTypeContrat().name().equalsIgnoreCase(type)) &&
                        (status == null || c.getStatus() == status) &&
                        (dateDebutMin == null || (c.getDateDebut() != null && !c.getDateDebut().isBefore(dateDebutMin)))
                        &&
                        (dateFinMax == null || (c.getDateFin() != null && !c.getDateFin().isAfter(dateFinMax))))
                .collect(Collectors.toList());
    }

    // Moyenne loyer mensuel
    public Double moyenneLoyerMensuel() {
        return contratsRepository.findAll().stream()
                .mapToDouble(c -> c.getLoyerMensuel() != null ? c.getLoyerMensuel() : 0)
                .average()
                .orElse(0.0);
    }

    // Nombre contrats actifs
    public long nombreContratsActifs() {
        return contratsRepository.findAll().stream()
                .filter(c -> c.getStatus() == StatusContrat.ACTIF)
                .count();
    }

    // Résiliation des contrats expirés
    @Transactional
    public void resilierContratsExpirés() {
        LocalDate today = LocalDate.now();

        List<Contrats> expires = contratsRepository.findAll().stream()
                .filter(c -> c.getDateFin() != null
                        && c.getDateFin().isBefore(today)
                        && c.getStatus() == StatusContrat.ACTIF)
                .collect(Collectors.toList());

        for (Contrats c : expires) {
            c.setStatus(StatusContrat.EXPIRE);
        }

        contratsRepository.saveAll(expires);
    }

}
