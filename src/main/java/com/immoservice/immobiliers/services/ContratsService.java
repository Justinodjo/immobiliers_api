package com.immoservice.immobiliers.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.immoservice.immobiliers.entities.BiensImmobiliers;
import com.immoservice.immobiliers.entities.Contrats;
import com.immoservice.immobiliers.entities.DetailsBiensImmobiliers;
import com.immoservice.immobiliers.entities.StatusContrat;
import com.immoservice.immobiliers.entities.TypeContrat;
import com.immoservice.immobiliers.entities.TypeNotifications;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.enums.ValidationStatus;
import com.immoservice.immobiliers.repository.BiensImmobiliersRepository;
import com.immoservice.immobiliers.repository.ContratsRepository;
import com.immoservice.immobiliers.repository.DetailsBiensImmobiliersRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ContratsService {

    private static final Logger logger = LoggerFactory.getLogger(ContratsService.class);
    @Autowired
    private BiensImmobiliersRepository biensImmobiliersRepository;

    @Autowired
    private DetailsBiensImmobiliersRepository detailsBiensImmobiliersRepository;

    @Autowired
    private ContratsRepository contratsRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private PdfService pdfGeneratorService;

    @Autowired
    private EmailService emailService;

    // 🟢 Ajoute cette ligne pour corriger ton erreur
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserInfoService userInfoService;

    private static final String PREFIX = "REF-HE-";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    // @Autowired
    // private TemplateEngine templateEngine;
    // ===========================
    // CRUD & Contrat management
    // ===========================
    @Transactional
    public Contrats saveContrat(Contrats contrats, String emailUtilisateur, boolean prixDetails) {

        // Log des informations reçues avant l'enregistrement
        logger.info(">>> Données reçues : {}", contrats);
        logger.debug(">>> Biens associés : {}", contrats.getBiensImmobiliers());
        // Récupération de l'utilisateur connecté
        UserInfo utilisateur = resolveUtilisateur(contrats);
        contrats.setUserInfo(utilisateur);
        // Initialisation automatique des champs
        initializerContratDefaults(contrats);

        // Calculer les frais d'annulation en fonction du type de contrat
        // if (contrats.getFraisAnnulation() == null) {
        // // Calcul des frais d'annulation et mise à jour du contrat

        // Double fraisAnnulation = annulerContrat(dateAnnulation);
        // contrats.setFraisAnnulation(fraisAnnulation); // Mise à jour des frais
        // d'annulation
        // }

        // Vérifie et associe les services à la réservation
        fetchAndSetBiensEtDetails(contrats);
        // Calcul du prix total

        // Calculer le prix de base selon le type de contrat
        double prixBase = calculerPrixParType(contrats);
        // Calculer toutes les commissions et prix total final
        appliquerCommissions(contrats, prixBase);
        // Sauvegarder la réservation
        Contrats savedContrat = contratsRepository.save(contrats);

        // Mise à jour notes biens et détails
        updateNotesBiensEtDetails(savedContrat);
        // Log après l'enregistrement
        logger.info(">>> contrat après enregistrement : {}", savedContrat);
        logger.info(">>> Biens immobiliers après enregistrement : {}", savedContrat.getBiensImmobiliers());
        logger.info(">>> Détails des biens associés : {}", savedContrat.getDetailsBiensImmobiliers());
        // ✅ NOUVEAU: Envoi des notifications et emails
        envoyerNotificationsCreationContrat(savedContrat);
        // Retourner la réservation enregistrée
        return savedContrat;
    }

    // ===========================
    // Suppression
    // ===========================
    public void deleteContrat(Long id) {
        contratsRepository.deleteById(id);
    }

    /**
     * ✅ NOUVEAU: Obtenir tous les gestionnaires disponibles
     */
    public List<UserInfo> getAllGestionnairesDisponibles() {
        return userInfoService.getAllGestionnaires();
    }

    /**
     * ✅ NOUVEAU: Obtenir les gestionnaires recommandés pour un bien
     * Basé sur la localisation et le score
     */
    public List<UserInfo> getGestionnairesRecommandesPourBien(Long bienId) {
        BiensImmobiliers bien = biensImmobiliersRepository.findById(bienId)
                .orElseThrow(() -> new EntityNotFoundException("Bien non trouvé"));

        String adresseBien = bien.getAdresse();

        // Récupérer les gestionnaires de la même zone avec score minimum
        return userInfoService.proposerGestionnairesPourProprietaire(
                bien.getUserInfo().getId(),
                50 // Score minimum
        );
    }

    private String generateReference() {
        StringBuilder sb = new StringBuilder(PREFIX);
        for (int i = 0; i < 6; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    /**
     * ✅ NOUVEAU: Initialiser les valeurs par défaut du contrat
     */
    public void initializerContratDefaults(Contrats contrat) {
        if (contrat.getDateDebut() == null) {
            LocalDate now = LocalDate.now();
            contrat.setDateDebut(now);
            contrat.setDateFin(now.getDayOfMonth() <= 20
                    ? LocalDate.of(now.getYear(), now.getMonth(), 20)
                    : LocalDate.of(now.plusMonths(1).getYear(), now.plusMonths(1).getMonth(), 20));
        }

        if (contrat.getReferenceContrat() == null) {
            contrat.setReferenceContrat(generateReference());
        }

        if (contrat.getAvance() == null && contrat.getEstimatedTotal() != null) {
            contrat.setAvance(contrat.getEstimatedTotal());
        }

        if (contrat.getStatus() == null) {
            contrat.setStatus(StatusContrat.ACTIF);
        }

        if (contrat.getValidationStatus() == null) {
            contrat.setValidationStatus(ValidationStatus.EN_ATTENTE);
        }
    }

    /**
     * ✅ NOUVEAU: Envoyer notification et email pour un contrat
     */
    public void envoyerNotificationEtEmailContrat(Contrats contrat, UserInfo destinataire, String typeAction) {
        String reference = contrat.getReferenceContrat();

        // Déterminer le message selon le rôle
        String messageNotif = construireMessageNotification(contrat, destinataire, typeAction);
        String sujetEmail = construireSujetEmail(contrat, typeAction);

        // 1. Envoyer notification in-app
        try {
            notificationService.creerNotification(
                    destinataire,
                    messageNotif,
                    sujetEmail,
                    typeAction.contains("GESTIONNAIRE") || typeAction.contains("PROPRIETAIRE")
                            ? TypeNotifications.RAPPEL
                            : TypeNotifications.INFO);
        } catch (Exception e) {
            logger.error("❌ Erreur notification pour {}: {}", destinataire.getEmail(), e.getMessage());
        }

        // 2. Envoyer email
        try {
            Map<String, Object> variables = construireVariablesEmail(contrat, destinataire, typeAction);
            String templateName = determinerTemplateEmail(typeAction);

            emailService.envoyerEmail(
                    destinataire.getEmail(),
                    sujetEmail,
                    templateName,
                    variables);

            logger.info("✉️ Email envoyé à: {}", destinataire.getEmail());
        } catch (Exception e) {
            logger.error("❌ Erreur envoi email à {}: {}", destinataire.getEmail(), e.getMessage());

            // Fallback: email simple
            try {
                String contenuSimple = construireEmailSimple(contrat, destinataire, typeAction);
                emailService.envoyerEmailSimple(
                        destinataire.getEmail(),
                        sujetEmail,
                        contenuSimple);
            } catch (Exception ex) {
                logger.error("❌ Erreur email simple: {}", ex.getMessage());
            }
        }
    }

    /**
     * ✅ NOUVEAU: Construire le message de notification
     */
    public String construireMessageNotification(Contrats contrat, UserInfo destinataire, String typeAction) {
        String reference = contrat.getReferenceContrat();
        String nomLocataire = contrat.getUserInfo().getNomComplet();

        return switch (typeAction) {
            case "CREATION" -> {
                boolean estGestionnaire = contrat.getGestionnaire() != null &&
                        contrat.getGestionnaire().getId().equals(destinataire.getId());

                if (estGestionnaire) {
                    yield String.format(
                            "🏢 Nouveau contrat à gérer: %s pour %s. Montant: %,.0f XOF",
                            reference, nomLocataire, contrat.getPrixTotal());
                } else {
                    yield String.format(
                            "🏠 Nouveau contrat établi: %s pour votre bien. Locataire: %s",
                            reference, nomLocataire);
                }
            }
            case "CONFIRMATION_LOCATAIRE" -> String.format(
                    "✅ Votre contrat %s a été enregistré avec succès. Montant: %,.0f XOF",
                    reference, contrat.getPrixTotal());
            case "VALIDATION" -> String.format(
                    "✅ Le contrat %s a été validé",
                    reference);
            case "ANNULATION" -> String.format(
                    "❌ Le contrat %s a été annulé",
                    reference);
            default -> String.format("📋 Mise à jour du contrat %s", reference);
        };
    }

    /**
     * ✅ NOUVEAU: Construire les variables pour le template email
     */
    public Map<String, Object> construireVariablesEmail(Contrats contrat, UserInfo destinataire, String typeAction) {
        Map<String, Object> variables = new HashMap<>();

        // Informations communes
        variables.put("nomDestinataire", destinataire.getNomComplet());
        variables.put("referenceContrat", contrat.getReferenceContrat());
        variables.put("nomLocataire", contrat.getUserInfo().getNomComplet());
        variables.put("emailLocataire", contrat.getUserInfo().getEmail());
        variables.put("telephoneLocataire", contrat.getUserInfo().getNumero());
        variables.put("dateDebut", contrat.getDateDebut());
        variables.put("dateFin", contrat.getDateFin());
        variables.put("montantTotal", String.format("%,.0f", contrat.getPrixTotal()));
        variables.put("typeContrat", contrat.getTypeContrat().toString());
        variables.put("status", contrat.getStatus().toString());

        // Informations sur les biens
        List<String> nomsBiens = new ArrayList<>();
        if (contrat.getBiensImmobiliers() != null) {
            nomsBiens.addAll(contrat.getBiensImmobiliers().stream()
                    .map(BiensImmobiliers::getNom)
                    .collect(Collectors.toList()));
        }
        if (contrat.getDetailsBiensImmobiliers() != null) {
            nomsBiens.addAll(
                    contrat.getDetailsBiensImmobiliers().stream()
                            .map(detail -> {
                                String nomBien = detail.getBiensImmobiliers() != null
                                        && detail.getBiensImmobiliers().getNom() != null
                                                ? detail.getBiensImmobiliers().getNom()
                                                : "";
                                String libelle = detail.getNomdetails() != null ? detail.getNomdetails() : "";
                                return nomBien + " - " + libelle;
                            })
                            .collect(Collectors.toList()));
        }

        variables.put("biens", String.join(", ", nomsBiens));

        // Commissions si gestionnaire
        if (contrat.getGestionnaire() != null &&
                contrat.getGestionnaire().getId().equals(destinataire.getId())) {
            variables.put("commissionGestionnaire",
                    String.format("%,.0f", contrat.getCommissionGestionnaire()));
            variables.put("estGestionnaire", true);
        } else {
            variables.put("estGestionnaire", false);
        }

        // Gestionnaire info si présent
        if (contrat.getGestionnaire() != null) {
            variables.put("nomGestionnaire", contrat.getGestionnaire().getNomComplet());
            variables.put("emailGestionnaire", contrat.getGestionnaire().getEmail());
        }

        return variables;
    }

    /**
     * ✅ NOUVEAU: Déterminer le template email à utiliser
     */
    private String determinerTemplateEmail(String typeAction) {
        return switch (typeAction) {
            case "CREATION" -> "email/nouveau-contrat";
            case "CONFIRMATION_LOCATAIRE" -> "email/confirmation-contrat";
            case "VALIDATION" -> "email/validation-contrat";
            case "ANNULATION" -> "email/annulation-contrat";
            default -> "email/notification-contrat";
        };
    }

    /**
     * ✅ NOUVEAU: Construire le sujet de l'email
     */
    private String construireSujetEmail(Contrats contrat, String typeAction) {
        return switch (typeAction) {
            case "CREATION" -> "Nouveau contrat établi - " + contrat.getReferenceContrat();
            case "CONFIRMATION_LOCATAIRE" -> "Confirmation de votre contrat - " + contrat.getReferenceContrat();
            case "VALIDATION" -> "Contrat validé - " + contrat.getReferenceContrat();
            case "ANNULATION" -> "Contrat annulé - " + contrat.getReferenceContrat();
            default -> "Mise à jour contrat - " + contrat.getReferenceContrat();
        };
    }

    /**
     * ✅ NOUVEAU: Construire un email simple (fallback)
     */
    private String construireEmailSimple(Contrats contrat, UserInfo destinataire, String typeAction) {
        StringBuilder email = new StringBuilder();

        email.append("Bonjour ").append(destinataire.getNomComplet()).append(",\n\n");

        switch (typeAction) {
            case "CREATION":
                boolean estGestionnaire = contrat.getGestionnaire() != null &&
                        contrat.getGestionnaire().getId().equals(destinataire.getId());

                if (estGestionnaire) {
                    email.append("Un nouveau contrat a été établi et vous êtes désigné comme gestionnaire.\n\n");
                    email.append("Commission gestionnaire: ")
                            .append(String.format("%,.0f XOF", contrat.getCommissionGestionnaire()))
                            .append("\n\n");
                } else {
                    email.append("Un nouveau contrat a été établi pour votre bien.\n\n");
                }
                break;

            case "CONFIRMATION_LOCATAIRE":
                email.append("Votre contrat de location a été enregistré avec succès.\n\n");
                break;
        }

        email.append("Détails du contrat:\n");
        email.append("- Référence: ").append(contrat.getReferenceContrat()).append("\n");
        email.append("- Locataire: ").append(contrat.getUserInfo().getNomComplet()).append("\n");
        email.append("- Date début: ").append(contrat.getDateDebut()).append("\n");
        email.append("- Date fin: ").append(contrat.getDateFin()).append("\n");
        email.append("- Montant total: ").append(String.format("%,.0f XOF", contrat.getPrixTotal())).append("\n");

        if (contrat.getGestionnaire() != null) {
            email.append("- Gestionnaire: ").append(contrat.getGestionnaire().getNomComplet()).append("\n");
        }

        email.append("\n");
        email.append("Cordialement,\n");
        email.append("L'équipe MainsDorPlus");

        return email.toString();
    }

    /**
     * ✅ NOUVEAU: Envoyer toutes les notifications de création de contrat
     */
    private void envoyerNotificationsCreationContrat(Contrats contrat) {
        // 1. Collecter tous les destinataires
        Set<UserInfo> destinataires = new HashSet<>();

        // Propriétaires des biens principaux
        if (contrat.getBiensImmobiliers() != null) {
            contrat.getBiensImmobiliers().forEach(bien -> {
                if (bien.getUserInfo() != null) {
                    destinataires.add(bien.getUserInfo());
                }
            });
        }

        // Propriétaires des biens détaillés
        if (contrat.getDetailsBiensImmobiliers() != null) {
            contrat.getDetailsBiensImmobiliers().forEach(detail -> {
                BiensImmobiliers bienParent = detail.getBiensImmobiliers();
                if (bienParent != null && bienParent.getUserInfo() != null) {
                    destinataires.add(bienParent.getUserInfo());
                }
            });
        }

        // Gestionnaire si présent
        if (contrat.getGestionnaire() != null) {
            destinataires.add(contrat.getGestionnaire());
        }

        // 2. Envoyer les notifications et emails
        destinataires.forEach(destinataire -> {
            envoyerNotificationEtEmailContrat(contrat, destinataire, "CREATION");
        });

        // 3. Notification pour le locataire
        if (contrat.getUserInfo() != null) {
            envoyerNotificationEtEmailContrat(contrat, contrat.getUserInfo(), "CONFIRMATION_LOCATAIRE");
        }

        logger.info("📧 Notifications envoyées à {} destinataire(s)", destinataires.size());
    }

    // ===========================
    // Recherche
    // ===========================
    public List<Contrats> findByKeyword(String texte) {
        if (texte == null || texte.isEmpty())
            return Collections.emptyList();
        String lower = texte.toLowerCase();
        return contratsRepository.findAll().stream()
                .filter(c -> (c.getUserInfo() != null && c.getUserInfo().getName().toLowerCase().contains(lower))
                        || (c.getBiensImmobiliers() != null && c.getBiensImmobiliers().stream()
                                .anyMatch(b -> b.getNom().toLowerCase().contains(lower)))
                        || (c.getReferenceContrat() != null && c.getReferenceContrat().toLowerCase().contains(lower)))
                .toList();
    }

    /**
     * ✅ NOUVEAU: Obtenir les gestionnaires par zone géographique
     */
    public List<UserInfo> getGestionnairesByZone(String adresse) {
        return userInfoService.getAllGestionnaires().stream()
                .filter(g -> g.getAdresse() != null &&
                        g.getAdresse().toLowerCase().contains(adresse.toLowerCase()))
                .collect(Collectors.toList());
    }

    // <-- Ajouter ceci (nouvelle méthode utilitaire)
    private void notifyContractChange(Contrats contrat, String action) {
        if (contrat == null)
            return;
        String contexte = "Contrat #" + contrat.getId();
        String message = switch (action) {
            case "CREATED" -> "Un nouveau contrat a été créé : " + contexte;
            case "CANCELLED" -> "Un contrat a été annulé : " + contexte;
            case "CONFIRMED_BY_MANAGER" -> "Le contrat a été confirmé par le gestionnaire : " + contexte;
            case "VALIDATED_BY_OWNER" -> "Le contrat a été validé par le propriétaire : " + contexte;
            case "VALIDATED_BY_TENANT" -> "Le contrat a été validé par le locataire : " + contexte;
            case "RENEWED" -> "Le contrat a été renouvelé : " + contexte;
            case "EXPIRED" -> "Le contrat a expiré : " + contexte;
            default -> "Mise à jour sur le contrat : " + contexte;
        };

        // Collecte des destinataires (propriétaires des biens + propriétaires des
        // détails)
        List<UserInfo> destinataires = new java.util.ArrayList<>();
        if (contrat.getBiensImmobiliers() != null) {
            contrat.getBiensImmobiliers().forEach(b -> {
                if (b.getUserInfo() != null)
                    destinataires.add(b.getUserInfo());
            });
        }
        if (contrat.getDetailsBiensImmobiliers() != null) {
            contrat.getDetailsBiensImmobiliers().forEach(d -> {
                BiensImmobiliers parent = d.getBiensImmobiliers();
                if (parent != null && parent.getUserInfo() != null)
                    destinataires.add(parent.getUserInfo());
            });
        }

        // ajouter gestionnaire s'il existe
        if (contrat.getGestionnaire() != null)
            destinataires.add(contrat.getGestionnaire());

        // destinaire principal (locataire/client)
        UserInfo locataire = contrat.getUserInfo();

        // enlever doublons
        List<UserInfo> uniques = destinataires.stream().filter(java.util.Objects::nonNull).distinct().toList();

        if (!uniques.isEmpty()) {
            notificationService.envoyerNotificationOrientee(contexte, uniques, message);
        }

        if (locataire != null) {
            notificationService.envoyerNotificationPrivee(locataire, message);
        }

        // log
        logger.info("notifyContractChange action={} dest={} locataire={} ", action, uniques.size(),
                locataire != null ? locataire.getEmail() : "none");
    }

    private void updateNotesBiensEtDetails(Contrats contrat) {
        if (contrat.getBiensImmobiliers() != null) {
            for (BiensImmobiliers bien : contrat.getBiensImmobiliers()) {
                int nombreContrats = contratsRepository.countByBiensImmobiliers_Id(bien.getId());
                bien.setNote(Math.min(nombreContrats * 2.0, 5.0));
                biensImmobiliersRepository.save(bien);
            }
        }
        if (contrat.getDetailsBiensImmobiliers() != null) {
            for (DetailsBiensImmobiliers detail : contrat.getDetailsBiensImmobiliers()) {
                int nombreContrats = contratsRepository.countByDetailsBiensImmobiliers_Id(detail.getId());
                detail.setNote(Math.min(nombreContrats * 2.0, 5.0));
                detailsBiensImmobiliersRepository.save(detail);
            }
        }
    }

    public UserInfo resolveUtilisateur(Contrats contrats) {
        // 1️⃣ Si un utilisateur est déjà lié au contrat, on le prend
        if (contrats.getUserInfo() != null && contrats.getUserInfo().getId() != null) {
            return userInfoRepository.findById(contrats.getUserInfo().getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Utilisateur non trouvé avec l'ID : " + contrats.getUserInfo().getId()));
        }

        // 2️⃣ Sinon, récupérer l'utilisateur courant depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName(); // l'username/email du principal
            return userInfoRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Utilisateur courant non trouvé avec l'email : " + email));
        }

        // 3️⃣ Aucun utilisateur trouvé
        throw new IllegalArgumentException("Aucun utilisateur associé au contrat.");
    }

    public void appliquerCommissions(Contrats contrats, double prixBase) {
        // 🧩 Détermination des commissions
        double commissionSysteme = 0.0;
        double commissionGestionnaire = 0.0;
        double tauxSysteme = 0.0;
        double tauxGestionnaire = 0.0;

        // Vérifie si au moins un bien du contrat est géré par un gestionnaire
        boolean gereParGestionnaire = contrats.getBiensImmobiliers() != null
                && !contrats.getBiensImmobiliers().isEmpty()
                && contrats.getBiensImmobiliers().stream().anyMatch(b -> b.getGestionnaire() != null);

        // Vérifier le type du contrat (vente ou location)
        boolean estVente = contrats.getTypeContrat() != null &&
                contrats.getTypeContrat().toString().toLowerCase().contains("vente");
        boolean estLocation = contrats.getTypeContrat() != null &&
                contrats.getTypeContrat().toString().toLowerCase().contains("location");

        // 🔹 3️⃣ Application des règles métiers
        if (!gereParGestionnaire) {
            // Cas propriétaire gère lui-même
            if (estVente) {
                tauxSysteme = 0.10; // 10%
            } else if (estLocation) {
                tauxSysteme = 0.07; // 7%
            }
            commissionSysteme = prixBase * tauxSysteme;
        } else {
            // Cas gestionnaire présent
            if (estVente) {
                tauxSysteme = 0.03; // 3% pour le système
                tauxGestionnaire = 0.07; // 7% pour le gestionnaire
            } else if (estLocation) {
                tauxSysteme = 0.02; // 2% pour le système
                tauxGestionnaire = 0.05; // 5% pour le gestionnaire
            }
            commissionSysteme = prixBase * tauxSysteme;
            commissionGestionnaire = prixBase * tauxGestionnaire;
        }

        // 🧾 4️⃣ Mise à jour des montants dans le contrat
        contrats.setCommissionSysteme(commissionSysteme);
        contrats.setCommissionGestionnaire(commissionGestionnaire);

        // Prix total final = base + commissions
        double prixFinal = prixBase + commissionSysteme + commissionGestionnaire;
        contrats.setPrixTotal(prixFinal);

        // Calcul du prix selon le type
        // double prixCalcule = calculerPrixParType(contrats, prixDetails);
        // contrats.setPrixTotal(prixCalcule);

        logger.info(">>> Calcul commissions - Base: {} | Système: {} | Gestionnaire: {} | Total: {}", prixBase,
                commissionSysteme, commissionGestionnaire, prixFinal);
    }

    public double calculerPrixParType(Contrats contrat) {
        if (contrat.getDetailsBiensImmobiliers() != null && !contrat.getDetailsBiensImmobiliers().isEmpty()) {
            return contrat.calculatePrixDetails();
        }
        return contrat.calculatePrixBiens();
    }

    // 🔹 Récupérer un utilisateur par son ID
    public UserInfo getById(Integer id) {
        return userInfoRepository.findById(id).orElse(null);
    }

    public void notifierCreationContrat(Contrats contrat) {
        String contexte = "Contrat #" + contrat.getId();
        // 🟩 Utiliser une liste mutable pour éviter l’erreur "must be final or
        // effectively final"
        List<UserInfo> destinataires = new java.util.ArrayList<>();
        // 🏠 Ajouter les propriétaires des biens principaux
        if (contrat.getBiensImmobiliers() != null) {
            contrat.getBiensImmobiliers().forEach(bien -> {
                if (bien.getUserInfo() != null) {
                    destinataires.add(bien.getUserInfo());
                }
            });
        }

        // 🏘️ Ajouter les propriétaires des détails (chambres, appartements, etc.)
        if (contrat.getDetailsBiensImmobiliers() != null) {
            contrat.getDetailsBiensImmobiliers().forEach(detail -> {
                BiensImmobiliers bienParent = detail.getBiensImmobiliers();
                if (bienParent != null && bienParent.getUserInfo() != null) {
                    destinataires.add(bienParent.getUserInfo());
                }
            });
        }

        // 👥 Supprimer les doublons
        List<UserInfo> uniques = destinataires.stream().distinct().toList();

        // 🔔 Envoyer la notification
        notificationService.envoyerNotificationOrientee(
                contexte,
                uniques,
                "Un nouveau contrat a été créé concernant votre bien.");

        // 🔔 Notification spécifique pour le locataire (si présent)
        if (contrat.getUserInfo() != null) {
            notificationService.envoyerNotificationPrivee(
                    contrat.getUserInfo(),
                    "Félicitations ! Votre contrat de location/achat (" + contexte + ") a été enregistré avec succès.");
        }
        // Log pour vérification
        logger.info("Notification envoyée à {} destinataire(s) pour {}", uniques.size(), contexte);
    }

    public List<Contrats> getContratsByUser(Long userId) {
        return contratsRepository.findByUserInfoId(userId);
    }

    public List<BiensImmobiliers> getBiensDisponibles() {
        return biensImmobiliersRepository.findAll().stream()
                .filter(b -> contratsRepository.findByBiensImmobiliersAndStatus(b, StatusContrat.ACTIF).isEmpty())
                .toList();
    }

    public List<Contrats> getContratsByGestionnaire(Long gestionnaireId) {
        return contratsRepository.findByGestionnaireId(gestionnaireId);
    }
    // private void updateBienNote(Contrats contrats) {
    // for (BiensImmobiliers bien : contrats.getBiensImmobiliers()) {
    // int nombreContrats =
    // contratsRepository.countByBiensImmobiliers_Id(bien.getId());
    // bien.setNote(Math.min(nombreContrats * 2.0, 5.0));
    // biensImmobiliersRepository.save(bien);
    // }

    // for (DetailsBiensImmobiliers detail : contrats.getDetailsBiensImmobiliers())
    // {
    // int nombreContrats =
    // contratsRepository.countByDetailsBiensImmobiliers_Id(detail.getId());
    // detail.setNote(Math.min(nombreContrats * 2.0, 5.0));
    // detailsBiensImmobiliersRepository.save(detail);
    // }
    // }

    // ===========================
    // Annulation
    // ===========================
    public void annulerContrat(Long contratId, LocalDate dateAnnulation) {
        // Récupérer le contrat depuis la base de données
        Contrats contrat = contratsRepository.findById(contratId)
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé pour l'ID : " + contratId));

        // Appeler la méthode annulerContrat() sur l'objet Contrats
        contrat.annulerContrat(dateAnnulation);

        // Construire les variables pour le template Thymeleaf
        Map<String, Object> variables = Map.of(
                "nomUtilisateur", contrat.getUserInfo().getName(),
                "idContrat", contrat.getId(),
                "dateAnnulation", dateAnnulation);

        // Appeler la méthode envoyerEmail avec les variables
        String emailUtilisateur = contrat.getUserInfo().getEmail();
        String sujet = "Votre contrat a été annulé";

        // Envoi de l'email en utilisant le template Thymeleaf
        emailService.envoyerEmail(emailUtilisateur, sujet, "email/annulation-contrat", variables);
        notifyContractChange(contrat, "CANCELLED");
        contratsRepository.save(contrat);
    }

    // public List<BiensImmobiliers> getBiensImmobiliersLoues(StatusContrat status)
    // {
    // return contratsRepository.findBiensImmobiliersLoues(LocalDate.now(),status);
    // }
    // Méthode pour récupérer les biens immobiliers loués (contrat actif)
    public List<BiensImmobiliers> getBiensImmobiliersLoues() {
        return contratsRepository.findBiensImmobiliersLoues(LocalDate.now(), StatusContrat.ACTIF);
    }

    public void fetchAndSetBiensEtDetails(Contrats contrats) {
        boolean hasBiens = contrats.getBiensImmobiliers() != null && !contrats.getBiensImmobiliers().isEmpty();
        boolean hasDetails = contrats.getDetailsBiensImmobiliers() != null
                && !contrats.getDetailsBiensImmobiliers().isEmpty();

        if (!hasBiens && !hasDetails) {
            throw new IllegalArgumentException("Aucun bien ni détail de bien fourni.");
        }

        // 🔹 Si des biens sont fournis
        if (hasBiens) {
            List<Long> biensIds = contrats.getBiensImmobiliers().stream()
                    .map(BiensImmobiliers::getId)
                    .collect(Collectors.toList());

            List<BiensImmobiliers> biens = biensImmobiliersRepository.findAllById(biensIds);
            if (biens.size() != biensIds.size()) {
                throw new EntityNotFoundException("Un ou plusieurs biens immobiliers fournis ne sont pas valides.");
            }

            contrats.setBiensImmobiliers(biens);
        }

        // 🔹 Si des détails sont fournis
        if (hasDetails) {
            List<Long> detailsIds = contrats.getDetailsBiensImmobiliers().stream()
                    .map(DetailsBiensImmobiliers::getId)
                    .collect(Collectors.toList());

            List<DetailsBiensImmobiliers> details = detailsBiensImmobiliersRepository.findAllById(detailsIds);
            if (details.size() != detailsIds.size()) {
                throw new EntityNotFoundException("Un ou plusieurs détails de biens fournis ne sont pas valides.");
            }

            contrats.setDetailsBiensImmobiliers(details);
        }

        logger.info(">>> Biens associés au contrat : {}", contrats.getBiensImmobiliers());
        logger.info(">>> Détails associés au contrat : {}", contrats.getDetailsBiensImmobiliers());
    }

    // Méthode pour récupérer les biens immobiliers résiliés
    public List<BiensImmobiliers> getBiensImmobiliersResilies() {
        return contratsRepository.findBiensImmobiliersLoues(LocalDate.now(), StatusContrat.RESILIE);
    }

    public void traiterContrat(Contrats contrat) {
        if (contrat.getGestionnaire() != null) {
            calculerToutesCommissions(contrat);
            // Ajoute au solde du gestionnaire, génère une facture, etc.
        }

        contratsRepository.save(contrat);
    }

    // ===========================
    // Validation Workflow
    // ===========================
    @Transactional
    public Contrats confirmerContratParGestionnaire(Long contratId) {
        Contrats contrat = contratsRepository.findById(contratId)
                .orElseThrow(() -> new EntityNotFoundException("Contrat introuvable"));

        contrat.confirmerParGestionnaire();
        Contrats saved = contratsRepository.save(contrat);
        notifyContractChange(saved, "CONFIRMED_BY_MANAGER");
        return saved;
    }

    public void calculerToutesCommissions(Contrats contrat) {
        appliquerCommissions(contrat, calculerPrixParType(contrat));
    }

    @Transactional(readOnly = true)
    public boolean verifierDocumentsContrat(Long contratId) {
        Contrats contrat = contratsRepository.findById(contratId)
                .orElseThrow(() -> new EntityNotFoundException("Contrat introuvable"));

        // Vérifie que tous les documents ont au moins un fichier associé
        return contrat.getDocuments() != null && !contrat.getDocuments().isEmpty() &&
                contrat.getDocuments().stream().allMatch(doc -> (doc.getFichier() != null) ||
                        (doc.getFichiers() != null && !doc.getFichiers().isEmpty()));
    }

    @Transactional
    public Contrats validerContratParProprietaire(Long contratId) {
        Contrats contrat = contratsRepository.findById(contratId)
                .orElseThrow(() -> new EntityNotFoundException("Contrat introuvable"));
        if (contrat.getValidationStatus() != ValidationStatus.CONFIRME_PAR_GESTIONNAIRE) {
            throw new IllegalStateException(
                    "Le contrat doit être confirmé par le gestionnaire avant validation par le propriétaire.");
        }
        // ✅ Vérification des documents obligatoires
        if (!verifierDocumentsContrat(contratId)) {
            throw new IllegalStateException("Tous les documents obligatoires doivent être fournis avant validation.");
        }
        contrat.validerParProprietaire();
        Contrats saved = contratsRepository.save(contrat);

        notifyContractChange(saved, "VALIDATED_BY_OWNER");
        return saved;
    }

    @Transactional
    public Contrats validerContratParLocataire(Long contratId) {
        Contrats contrat = contratsRepository.findById(contratId)
                .orElseThrow(() -> new EntityNotFoundException("Contrat introuvable"));
        if (contrat.getValidationStatus() != ValidationStatus.VALIDEE_PAR_PROPRIETAIRE) {
            throw new IllegalStateException(
                    "Le contrat doit être validé par le propriétaire avant validation par le locataire.");
        }

        contrat.validerParLocataire();
        Contrats saved = contratsRepository.save(contrat);

        notifyContractChange(saved, "VALIDATED_BY_TENANT");
        return saved;
    }

    // ===========================
    // Commission
    // ===========================
    @Transactional
    public void assignerGestionnaireEtCalculerCommission(Contrats contrats, UserInfo gestionnaire) {
        UserInfo gestionnaireComplet = userInfoService.getUser(gestionnaire.getId());

        contrats.setGestionnaire(gestionnaireComplet);
        calculerToutesCommissions(contrats);

        Contrats saved = contratsRepository.save(contrats);

        // Envoyer notification au gestionnaire
        envoyerNotificationEtEmailContrat(saved, gestionnaireComplet, "CREATION");

        logger.info("✅ Gestionnaire {} assigné au contrat {}",
                gestionnaireComplet.getNomComplet(), contrats.getReferenceContrat());
    }

    public int calculerScoreUtilisateur(UserInfo user) {
        List<Contrats> contrats = contratsRepository.findByUserInfo(user);
        Map<TypeContrat, Integer> scoreMap = Map.of(
                TypeContrat.MEUBLEE, 10,
                TypeContrat.NON_MEUBLEE, 8,
                TypeContrat.SAISONNIERE, 5,
                TypeContrat.COMMERCIALE, 12,
                TypeContrat.PROFESSIONNELLE, 15,
                TypeContrat.ETUDIANT, 6,
                TypeContrat.OPTION_ACHAT, 18,
                TypeContrat.ACCESSION, 20);

        int score = contrats.stream().mapToInt(contrat -> {
            int typeScore = scoreMap.getOrDefault(contrat.getTypeContrat(), 0);
            int bonusStatus = contrat.getStatus() == StatusContrat.ACTIF ? 5 : 0;
            long durationBonus = ChronoUnit.DAYS.between(contrat.getDateDebut(), contrat.getDateFin()) > 180 ? 5 : 0;
            double priceBonus = contrat.getPrixTotal() != null
                    ? (contrat.getPrixTotal() > 100_000 ? 10 : contrat.getPrixTotal() > 50_000 ? 5 : 0)
                    : 0;
            return typeScore + bonusStatus + (int) durationBonus + (int) priceBonus;
        }).sum();

        return score;
    }

    public double calculerRevenuGestionnaire(UserInfo gestionnaire) {
        return contratsRepository.findByGestionnaire(gestionnaire).stream()
                .mapToDouble(c -> extracted(c))
                .sum();
    }

    private double extracted(Contrats c) {
        return c.getCommission() != null ? c.getCommission() : 0.0;
    }

    @Transactional
    public void traiterContratsExpires() {
        LocalDate today = LocalDate.now();
        List<Contrats> contratsExpires = contratsRepository.findAll().stream()
                .filter(c -> c.getDateFin() != null && c.getDateFin().isBefore(today)
                        && c.getStatus() == StatusContrat.ACTIF)
                .collect(Collectors.toList());

        contratsExpires.forEach(c -> c.setStatus(StatusContrat.EXPIRE));
        contratsRepository.saveAll(contratsExpires);
    }

    public byte[] genererContratPDF(Contrats contrat, String logoPath) throws Exception {
        try {
            String nomClient = contrat.getUserInfo().getName();
            String typeContrat = contrat.getTypeContrat().name();
            String bien = contrat.getBiensImmobiliers().isEmpty() ? "Non défini"
                    : contrat.getBiensImmobiliers().get(0).getNom(); // ou une description

            return pdfGeneratorService.generate(typeContrat, contrat);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du contrat PDF", e);
        }
    }

    public List<Contrats> findByUserInfoEmail(String email) {
        return contratsRepository.findByUserInfoEmail(email);
    }

    @Transactional
    public void archiverContratsExpires() {
        LocalDate aujourdHui = LocalDate.now();

        // Récupère uniquement les contrats expirés mais pas encore archivés
        List<Contrats> contratsExpires = contratsRepository
                .findByDateFinBeforeAndStatusNot(aujourdHui, StatusContrat.ACTIF); // Remplacez "StatusContrat.ACTIF" si
                                                                                   // vous avez un enum

        for (Contrats contrat : contratsExpires) {
            contrat.setStatus(StatusContrat.EXPIRE); // Utilisez l'enum plutôt que String
        }

        // Sauvegarde en batch
        contratsRepository.saveAll(contratsExpires);
    }

    public List<Contrats> rechercherParType(TypeContrat typeContrat) {
        return contratsRepository.findByTypeContrat(typeContrat);
    }

    public List<Contrats> rechercherParStatus(String status) {
        return contratsRepository.findByStatus(status);
    }

    // ===========================
    // Renouvellement & Expiré
    // ===========================
    @Transactional
    public Contrats renouvelerContrat(Long idContrat, LocalDate nouvelleDateFin) {
        Contrats contrat = contratsRepository.findById(idContrat)
                .orElseThrow(() -> new EntityNotFoundException("Contrat introuvable"));

        // Vérification de la disponibilité du bien pendant la nouvelle période
        LocalDate today = LocalDate.now();
        if (nouvelleDateFin.isBefore(today)) {
            throw new IllegalArgumentException("La nouvelle date de fin doit être postérieure à aujourd'hui.");
        }

        // Logic to check if the property is available during this period
        List<Contrats> existingContrats = contratsRepository.findByBiensImmobiliersAndDateFinAfter(
                contrat.getBiensImmobiliers().get(0), today);
        if (!existingContrats.isEmpty()) {
            throw new IllegalStateException("Le bien n'est pas disponible pendant cette période.");
        }

        contrat.setDateDebut(today);
        contrat.setDateFin(nouvelleDateFin);
        contrat.setStatus(StatusContrat.ACTIF);
        Contrats saved = contratsRepository.save(contrat);
        notifyContractChange(saved, "RENEWED");
        return saved;
    }

    // Résiliation des contrats expirés
    @Transactional
    public void resilierContratsExpires() {
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
        expires.forEach(c -> notifyContractChange(c, "EXPIRED"));
    }

    public double calculerRevenuParUtilisateur(UserInfo user) {
        return contratsRepository.findByUserInfo(user).stream()
                .mapToDouble(c -> c.getPrixTotal() != null ? c.getPrixTotal() : 0.0)
                .sum();
    }

    // ===========================
    // PDF / CSV / Statistiques
    // ===========================
    // Export CSV
    public String exportContratsAsCSV(LocalDate dateDebutMin, LocalDate dateFinMax, TypeContrat typeContrat,
            StatusContrat status) {
        List<Contrats> contrats = contratsRepository.findAll().stream()
                .filter(c -> (dateDebutMin == null || !c.getDateDebut().isBefore(dateDebutMin)) &&
                        (dateFinMax == null || !c.getDateFin().isAfter(dateFinMax)) &&
                        (typeContrat == null || c.getTypeContrat() == typeContrat) &&
                        (status == null || c.getStatus() == status))
                .collect(Collectors.toList());

        StringBuilder csv = new StringBuilder("ID,Type,Statut,Client,Bien,Début,Fin,Prix\n");
        for (Contrats contrat : contrats) {
            String client = contrat.getUserInfo() != null ? contrat.getUserInfo().getName() : "Inconnu";
            String bien = contrat.getBiensImmobiliers().isEmpty() ? "Aucun"
                    : contrat.getBiensImmobiliers().get(0).getNom();
            csv.append("%d,%s,%s,%s,%s,%s,%s,%.2f\n".formatted(
                    contrat.getId(),
                    contrat.getTypeContrat(),
                    contrat.getStatus().name(),
                    client,
                    bien,
                    contrat.getDateDebut(),
                    contrat.getDateFin(),
                    contrat.getPrixTotal() != null ? contrat.getPrixTotal() : 0.0));
        }

        return csv.toString();
    }

    // ===========================
    // Utilitaires
    // ===========================
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

    public boolean verifierDisponibiliteContrat(Long contratId) {
        Contrats contrat = contratsRepository.findById(contratId)
                .orElseThrow(() -> new EntityNotFoundException("Contrat introuvable"));
        return contrat.getStatus() == StatusContrat.ACTIF;
    }

    public byte[] genererFacturePDF(Long contratId) throws Exception {
        Contrats contrat = contratsRepository.findById(contratId)
                .orElseThrow(() -> new EntityNotFoundException("Contrat introuvable"));

        // Charger le logo depuis le classpath
        String logoPath = null;
        try {
            // On copie le logo depuis les ressources dans un fichier temporaire
            InputStream logoStream = getClass().getResourceAsStream("/static/logo.png");
            if (logoStream != null) {
                File tempLogo = File.createTempFile("logo", ".png");
                Files.copy(logoStream, tempLogo.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logoPath = tempLogo.getAbsolutePath();
            }
        } catch (IOException e) {
            // on peut continuer sans logo si non trouvé
            System.err.println("Logo non trouvé : " + e.getMessage());
        }

        return genererContratPDF(contrat, logoPath);
    }

    // Moyenne loyer mensuel
    public Double moyenneLoyerMensuel() {
        return contratsRepository.findAll().stream()
                .mapToDouble(c -> c.getLoyerMensuel() != null ? c.getLoyerMensuel() : 0)
                .average()
                .orElse(0.0);
    }

    public List<Contrats> getContratsActifs() {
        return contratsRepository.findByStatus(StatusContrat.ACTIF);
    }

    public List<Contrats> getContratsExpires() {
        return contratsRepository.findByStatus(StatusContrat.EXPIRE);
    }

    public List<Contrats> getContratsEnAttente() {
        return contratsRepository.findByValidationStatus(ValidationStatus.EN_ATTENTE);
    }

    public List<Contrats> getContratsEnAttenteValidation() {
        return contratsRepository.findByValidationStatus(ValidationStatus.EN_ATTENTE);
    }

    // Nombre contrats actifs
    public long nombreContratsActifs() {
        return contratsRepository.findAll().stream()
                .filter(c -> c.getStatus() == StatusContrat.ACTIF)
                .count();
    }

    public List<Contrats> rechercherContrats(TypeContrat type, StatusContrat status, LocalDate dateDebutMin,
            LocalDate dateFinMax) {
        return contratsRepository.findContrats(type, status, dateDebutMin, dateFinMax);
    }

    public List<BiensImmobiliers> getBiensLouesParUtilisateur(UserInfo utilisateur) {
        if (utilisateur == null) {
            throw new IllegalArgumentException("Utilisateur ne peut pas être null.");
        }
        // Récupère tous les contrats actifs de l'utilisateur
        List<Contrats> contratsActifs = contratsRepository.findByUserInfoAndStatus(utilisateur, StatusContrat.ACTIF);

        // Récupère tous les biens principaux et détails associés
        return contratsActifs.stream()
                .flatMap(contrat -> {
                    Stream<BiensImmobiliers> biens = contrat.getBiensImmobiliers().stream();
                    Stream<BiensImmobiliers> details = contrat.getDetailsBiensImmobiliers().stream()
                            .map(DetailsBiensImmobiliers::getBiensImmobiliers);
                    return Stream.concat(biens, details);
                })
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Contrats> getContratsParUtilisateur(UserInfo user) {
        if (user == null) {
            throw new IllegalArgumentException("Utilisateur ne peut pas être null.");
        }
        return contratsRepository.findByUserInfo(user);
    }

    @Transactional(readOnly = true)
    public Contrats getContratById(Long id) {
        return contratsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contrat non trouvé pour l'ID : " + id));
    }

}
