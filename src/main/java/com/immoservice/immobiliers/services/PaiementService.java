package com.immoservice.immobiliers.services;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.immoservice.immobiliers.entities.Contrats;
import com.immoservice.immobiliers.entities.Paiements;
import com.immoservice.immobiliers.entities.StatutPaiement;
import com.immoservice.immobiliers.repository.ContratsRepository;
import com.immoservice.immobiliers.repository.PaiementsRepository;

@Service
public class PaiementService {
    private static final Logger logger = LoggerFactory.getLogger(PaiementService.class);
    private static final String API_KEY = "sk_live_uKcXC79oLvQReXGou2S691ps";
    private static final String API_URL = "https://api.fedapay.com/v1/transactions";

    @Autowired
    private PaiementsRepository paiementsRepository;

    @Autowired
    private ContratsRepository contratsRepository;

    @Autowired
    private PaiementDistributionService distributionService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PaiementQueryService paiementQueryService;

    // 🔹 Générer une référence unique
    private String generateReference() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // 🔹 Attacher les relations Contrat / Locataire si manquantes
    private void attachRelations(Paiements paiement) {
        if (paiement.getContrats() != null && paiement.getContrats().getId() != null) {
            contratsRepository.findById(paiement.getContrats().getId())
                    .ifPresent(paiement::setContrats);
        }
    }

    /**
     * 🔹 Créer un paiement manuel
     */
    @Transactional
    public Paiements createPaiement(Paiements paiement) {

        Long contratId = paiement.getContrats() != null ? paiement.getContrats().getId() : null;
        Contrats contrat = contratsRepository.findById(contratId)
                .orElseThrow(() -> new IllegalArgumentException("Contrat introuvable"));
        // Vérifier la logique métier : soit biens principaux, soit détails
        if (!contrat.estContratValide()) {
            throw new IllegalStateException(
                    "Le contrat doit être lié soit à des biens principaux, soit à des détails, pas les deux.");
        }
        // Générer une référence unique
        if (paiement.getReference() == null || paiement.getReference().isEmpty()) {
            paiement.setReference(generateReference());
        }

        // Vérifier et attacher les relations
        attachRelations(paiement);

        // Définir le statut par défaut
        if (paiement.getStatut() == null) {
            paiement.setStatut(StatutPaiement.EN_ATTENTE);
        }

        // Vérifier si le paiement est en retard
        if (paiement.getDateEcheance() != null &&
                paiement.getDateEcheance().isBefore(LocalDate.now()) &&
                paiement.getStatut() == StatutPaiement.EN_ATTENTE) {
            paiement.setStatut(StatutPaiement.EN_RETARD);
        }

        Paiements saved = paiementsRepository.save(paiement);

        // Notifier le locataire
        if (saved.getPayeur() != null) {
            String message = String.format(
                    "Un nouveau paiement de %s XOF est attendu pour le %s",
                    saved.getMontant(),
                    saved.getDateEcheance());
            notificationService.envoyerNotificationPrivee(saved.getPayeur(), message);
        }

        logger.info("Paiement créé avec succès : {}", saved.getReference());
        return saved;
    }

    /**
     * 🔹 Étape 1 : Création du paiement côté FedaPay + enregistrement local
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional
    public String initierPaiement(Long contratId, double montant, String email, String nom) {

        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro");
        }

        Contrats contrat = contratsRepository.findById(contratId)
                .orElseThrow(() -> new IllegalArgumentException("Contrat introuvable"));

        // Vérifier la logique métier : soit biens principaux, soit détails
        if (!contrat.estContratValide()) {
            throw new IllegalStateException(
                    "Le contrat doit être lié soit à des biens principaux, soit à des détails, pas les deux.");
        }

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", montant);
        requestBody.put("currency", "XOF");
        requestBody.put("description", "Paiement du contrat #" + contratId);
        requestBody.put("callback_url", "https://ton-backend.com/api/paiements/callback");
        requestBody.put("cancel_url", "https://ton-frontend.com/paiement/annule");

        Map<String, Object> customer = new HashMap<>();
        customer.put("firstname", nom);
        customer.put("email", email);
        requestBody.put("customer", customer);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                Map<String, Object> transaction = (Map<String, Object>) responseBody.get("transaction");

                // 🔸 Créer un enregistrement local du paiement
                Paiements paiement = new Paiements();
                paiement.setContrats(contrat);
                paiement.setMontant(montant);
                paiement.setMois(LocalDate.now().getMonthValue());
                paiement.setAnnee(LocalDate.now().getYear());
                paiement.setStatut(StatutPaiement.EN_ATTENTE);
                paiement.setTypePaiement("FedaPay");
                paiement.setDatePaiement(Date.valueOf(LocalDate.now()));

                paiementsRepository.save(paiement);

                // 🔸 Retourner l’URL de paiement FedaPay
                return transaction.get("url").toString();
            } else {
                return "Erreur FedaPay : " + response.getStatusCode();
            }

        } catch (RestClientException e) {
            return "Erreur : " + e.getMessage();
        }
    }

    /**
     * 🔹 Callback reçu de FedaPay
     */
    @Transactional
    public void traiterCallback(Map<String, Object> payload) {
        String status = (String) payload.get("status");
        String transactionFedapayId = payload.get("id").toString();

        Paiements paiement = paiementsRepository.findByTransactionFedapayId(transactionFedapayId);
        if (paiement == null) {
            logger.error("⚠️ Paiement introuvable pour la transaction : {}", transactionFedapayId);
            return;
        }

        switch (status.toLowerCase()) {
            case "approved":
                paiement.setStatut(StatutPaiement.PAYE);
                paiement.setDatePaiement(Date.valueOf(LocalDate.now()));
                paiementsRepository.save(paiement);

                // Distribuer le paiement
                distributionService.distribuerPaiement(paiement);

                // Notifier
                // Notifier le bénéficiaire (celui qui reçoit le paiement)
                if (paiement.getBeneficiaire() != null) {
                    String message = String.format(
                            "Vous avez reçu un paiement de %,.2f XOF pour le contrat #%d.",
                            paiement.getMontant(),
                            paiement.getContrats() != null ? paiement.getContrats().getId() : 0);
                    notificationService.envoyerNotificationPrivee(paiement.getBeneficiaire(), message);
                }

                // Notifier le payeur (celui qui a payé)
                if (paiement.getPayeur() != null) {
                    String message = String.format(
                            "Votre paiement de %,.2f XOF a été validé avec succès.",
                            paiement.getMontant());
                    notificationService.envoyerNotificationPrivee(paiement.getPayeur(), message);
                }
                logger.info("Paiement FedaPay approuvé : {}", transactionFedapayId);
                break;

            case "canceled":
                paiement.setStatut(StatutPaiement.ANNULE);
                paiementsRepository.save(paiement);
                logger.info("Paiement FedaPay annulé : {}", transactionFedapayId);
                break;

            default:
                logger.warn("Statut FedaPay inconnu : {}", status);
        }
    }

    /**
     * ✅ Récupérer tous les paiements
     */
    public List<Paiements> getAllPaiements() {
        return paiementsRepository.findAll();
    }

    /**
     * ✅ Récupérer un paiement par ID
     */
    public Paiements getPaiementById(Long id) {
        return paiementsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement introuvable avec l'ID: " + id));
    }

    /**
     * ✅ Supprimer un paiement
     */
    @Transactional
    public void deletePaiement(Long id) {
        Paiements paiement = getPaiementById(id);
        paiementsRepository.delete(paiement);
    }

    /**
     * ✅ Mettre à jour un paiement
     */
    @Transactional
    public Paiements updatePaiement(Long id, Paiements newPaiement) {
        Paiements existing = getPaiementById(id);
        existing.setMontant(newPaiement.getMontant());
        existing.setDatePaiement(newPaiement.getDatePaiement());
        existing.setStatut(newPaiement.getStatut());
        existing.setTypePaiement(newPaiement.getTypePaiement());
        existing.setMontantPaye(newPaiement.getMontantPaye());
        return paiementsRepository.save(existing);
    }

    /**
     * ✅ Marquer un paiement comme payé
     */
    @Transactional
    public Paiements marquerPaye(Long id, String modePaiement, LocalDate datePaiement, Double montantPaye,
            String numeroTransaction) {
        Paiements paiement = getPaiementById(id);
        paiement.setStatut(StatutPaiement.PAYE);
        paiement.setTypePaiement(modePaiement);
        paiement.setDatePaiement(Date.valueOf(datePaiement));
        paiement.setMontantPaye(montantPaye);
        paiement.setTransactionFedapayId(numeroTransaction);
        Paiements saved = paiementsRepository.save(paiement);

        // Distribution
        distributionService.distribuerPaiement(saved);
        return saved;
    }

    /**
     * ✅ Marquer plusieurs paiements comme payés
     */
    @Transactional
    public List<Paiements> marquerPayesMultiple(List<Long> ids, String modePaiement, LocalDate datePaiement) {
        List<Paiements> updated = new ArrayList<>();
        for (Long id : ids) {
            updated.add(marquerPaye(id, modePaiement, datePaiement, null, null));
        }
        return updated;
    }

    /**
     * ✅ Envoyer un rappel de paiement
     */
    @Transactional
    public void envoyerRappel(Long id) {
        Paiements paiement = getPaiementById(id);
        if (paiement.getContrats() != null && paiement.getContrats().getUserInfo() != null) {
            notificationService.envoyerNotificationPrivee(
                    paiement.getContrats().getUserInfo(),
                    "Rappel : votre paiement de " + paiement.getMontant() + " XOF est attendu.");
            paiement.setDateDernierRappel(java.time.LocalDateTime.now());
            paiementsRepository.save(paiement);
        }
    }

    @Transactional
    public void envoyerRappelsMultiple(List<Long> ids) {
        ids.forEach(this::envoyerRappel);
    }

    /**
     * ✅ Récupérer paiements par statut
     */
    public List<Paiements> getPaiementsByStatut(StatutPaiement statut) {
        return paiementsRepository.findByStatut(statut);
    }

    /**
     * ✅ Récupérer paiements par locataire
     */
    public List<Paiements> getPaiementsByLocataire(Long locataireId) {
        return paiementsRepository.findByContrats_UserInfo_Id(locataireId);
    }

    /**
     * ✅ Récupérer paiements par bien
     */
    public List<Paiements> getPaiementsByBien(Long bienId) {
        return paiementQueryService.parBien(bienId);
    }

    public List<Paiements> getPaiementsByDetail(Long detailId) {
        return paiementQueryService.parDetail(detailId);
    }

    public List<Paiements> getPaiementsByBienOuDetail(Long bienId) {
        return paiementQueryService.parBienOuDetail(bienId);
    }

    /**
     * ✅ Statistiques simples
     */
    public Map<String, Object> getStatistiques() {
        List<Paiements> paiements = paiementsRepository.findAll();
        double total = paiements.stream().mapToDouble(p -> p.getMontant() != null ? p.getMontant() : 0).sum();
        long payes = paiements.stream().filter(p -> p.getStatut() == StatutPaiement.PAYE).count();
        long enAttente = paiements.stream().filter(p -> p.getStatut() == StatutPaiement.EN_ATTENTE).count();

        return Map.of(
                "totalPaiements", paiements.size(),
                "totalMontant", total,
                "payes", payes,
                "enAttente", enAttente);
    }

    /**
     * ✅ Exporter en Excel
     */
    public byte[] exportToExcel(Map<String, Object> filters) throws Exception {
        List<Paiements> paiements = paiementsRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Paiements");

        Row header = sheet.createRow(0);
        String[] columns = { "ID", "Montant", "Statut", "Date Paiement", "Type Paiement" };
        for (int i = 0; i < columns.length; i++)
            header.createCell(i).setCellValue(columns[i]);

        int rowNum = 1;
        for (Paiements p : paiements) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getId());
            row.createCell(1).setCellValue(p.getMontant() != null ? p.getMontant() : 0);
            row.createCell(2).setCellValue(p.getStatut().toString());
            row.createCell(3).setCellValue(p.getDatePaiement() != null ? p.getDatePaiement().toString() : "");
            row.createCell(4).setCellValue(p.getTypePaiement());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    /**
     * ✅ Vérifier les paiements en retard
     */
    @Transactional
    public void verifierPaiementsEnRetard() {
        List<Paiements> paiements = paiementsRepository.findAll();
        for (Paiements p : paiements) {
            if (p.estEnRetard()) {
                p.setStatut(StatutPaiement.EN_RETARD);
                paiementsRepository.save(p);
            }
        }
    }
}
