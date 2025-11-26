package com.immoservice.immobiliers.services;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.immoservice.immobiliers.dto.EvenementHistorique;
import com.immoservice.immobiliers.entities.BiensImmobiliers;
import com.immoservice.immobiliers.entities.Contrats;
import com.immoservice.immobiliers.entities.Intervention;
import com.immoservice.immobiliers.entities.StatusContrat;
import com.immoservice.immobiliers.repository.BiensImmobiliersRepository;
import com.immoservice.immobiliers.repository.ContratsRepository;
import com.immoservice.immobiliers.repository.InterventionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly = true)
public class HistoriqueService {
    private static final Logger logger = LoggerFactory.getLogger(HistoriqueService.class);

    @Autowired
    private ContratsRepository contratsRepository;

    @Autowired
    private BiensImmobiliersRepository biensImmobiliersRepository;

    @Autowired
    private InterventionRepository interventionRepository;

    public List<EvenementHistorique> getAllHistorique() {
        List<EvenementHistorique> historique = new ArrayList<>();

        // 1. Événements depuis les contrats
        historique.addAll(genererEvenementsContrats());

        // 2. Événements depuis les interventions (travaux/incidents)
        historique.addAll(genererEvenementsInterventions());

        // Trier par date décroissante
      historique.sort(Comparator.comparing(EvenementHistorique::getDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed());


        logger.info("Historique complet généré : {} événements", historique.size());
        return historique;
    }

    /**
     * Génère les événements à partir des contrats
     */
    private List<EvenementHistorique> genererEvenementsContrats() {
        List<Contrats> contrats = contratsRepository.findAll();
        List<EvenementHistorique> events = new ArrayList<>();

        for (Contrats contrat : contrats) {
            // Événement ENTREE (date début)
            if (contrat.getDateDebut() != null) {
                EvenementHistorique entree = new EvenementHistorique();
                entree.setId(contrat.getId() * 1000L); // ID unique
                entree.setType("entree");
                entree.setTitre("Entrée en location");
                entree.setDescription("Début du contrat " + contrat.getReferenceContrat());
                entree.setDate(contrat.getDateDebut().atStartOfDay());
                entree.setContratId(contrat.getId());

                if (contrat.getUserInfo() != null) {
                    entree.setLocataireId(contrat.getUserInfo().getId().longValue());
                    entree.setLocataire(contrat.getUserInfo().getNomComplet());
                }

                if (!contrat.getBiensImmobiliers().isEmpty()) {
                    BiensImmobiliers bien = contrat.getBiensImmobiliers().get(0);
                    entree.setBienId(bien.getId());
                    entree.setBien(bien.getNom());
                }

                entree.setStatut(contrat.getStatus() == StatusContrat.ACTIF ? "en_cours" : "termine");
                entree.setHasDocument(contrat.getDocuments() != null && !contrat.getDocuments().isEmpty());

                events.add(entree);
            }

            // Événement SORTIE (date fin si contrat terminé/résilié)
            if (contrat.getDateFin() != null &&
                    (contrat.getStatus() == StatusContrat.EXPIRE ||
                            contrat.getStatus() == StatusContrat.RESILIE)) {

                EvenementHistorique sortie = new EvenementHistorique();
                sortie.setId(contrat.getId() * 1000L + 1);
                sortie.setType("sortie");
                sortie.setTitre("Sortie de location");
                sortie.setDescription("Fin du contrat " + contrat.getReferenceContrat());
                sortie.setDate(contrat.getDateFin().atTime(23, 59));
                sortie.setContratId(contrat.getId());

                if (contrat.getUserInfo() != null) {
                    sortie.setLocataireId(contrat.getUserInfo().getId().longValue());
                    sortie.setLocataire(contrat.getUserInfo().getNomComplet());
                }

                if (!contrat.getBiensImmobiliers().isEmpty()) {
                    BiensImmobiliers bien = contrat.getBiensImmobiliers().get(0);
                    sortie.setBienId(bien.getId());
                    sortie.setBien(bien.getNom());
                }

                sortie.setStatut("termine");

                events.add(sortie);
            }

            // Événement PAIEMENT (si loyer mensuel défini)
            if (contrat.getLoyerMensuel() != null && contrat.getLoyerMensuel() > 0) {
                EvenementHistorique paiement = new EvenementHistorique();
                paiement.setId(contrat.getId() * 1000L + 2);
                paiement.setType("paiement");
                paiement.setTitre("Paiement de loyer");
                paiement.setDescription("Loyer mensuel du contrat " + contrat.getReferenceContrat());
                paiement.setDate(contrat.getDateDebut().atStartOfDay());
                paiement.setContratId(contrat.getId());
                paiement.setMontant(contrat.getLoyerMensuel());

                if (contrat.getUserInfo() != null) {
                    paiement.setLocataireId(contrat.getUserInfo().getId().longValue());
                    paiement.setLocataire(contrat.getUserInfo().getNomComplet());
                }

                if (!contrat.getBiensImmobiliers().isEmpty()) {
                    BiensImmobiliers bien = contrat.getBiensImmobiliers().get(0);
                    paiement.setBienId(bien.getId());
                    paiement.setBien(bien.getNom());
                }

                paiement.setStatut("termine");

                events.add(paiement);
            }
        }

        return events;
    }

   /**
     * Génère les événements à partir des interventions
     */
    private List<EvenementHistorique> genererEvenementsInterventions() {
        List<Intervention> interventions = interventionRepository.findAll();
        List<EvenementHistorique> events = new ArrayList<>();

        for (Intervention intervention : interventions) {
            EvenementHistorique event = new EvenementHistorique();
            event.setId(intervention.getId() + 500000L);

            String type = (intervention.getTypeIntervention() != null)
                    ? intervention.getTypeIntervention().toLowerCase()
                    : "incident";
            event.setType(type.contains("travaux") ? "travaux" : "incident");

            event.setTitre(intervention.getTypeIntervention());
            event.setDescription(intervention.getDescription());

            LocalDateTime dateEvent = (intervention.getDateSignalement() != null)
                    ? intervention.getDateSignalement().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                    : LocalDateTime.now();
            event.setDate(dateEvent);

            if (intervention.getBiensImmobilers() != null) {
                event.setBienId(intervention.getBiensImmobilers().getId());
                event.setBien(intervention.getBiensImmobilers().getNom());
            }

            event.setMontant(intervention.getCoutReel() != null ? intervention.getCoutReel().doubleValue() : null);
            event.setStatut(intervention.getStatut() != null ? intervention.getStatut().name().toLowerCase() : "en_attente");
            event.setHasDocument(intervention.getFichiers() != null && !intervention.getFichiers().isEmpty());

            events.add(event);
        }

        return events;
    }


   /**
     * Récupérer l'historique filtré
     */
    public List<EvenementHistorique> getHistoriqueFiltre(
            Long locataireId, Long bienId, String typeEvenement,
            String statut, LocalDate dateDebut, LocalDate dateFin, Integer annee) {

        return getAllHistorique().stream()
                .filter(e -> locataireId == null || locataireId.equals(e.getLocataireId()))
                .filter(e -> bienId == null || bienId.equals(e.getBienId()))
                .filter(e -> typeEvenement == null || typeEvenement.isEmpty() || typeEvenement.equals(e.getType()))
                .filter(e -> statut == null || statut.isEmpty() || statut.equals(e.getStatut()))
                .filter(e -> dateDebut == null || (e.getDate() != null && !e.getDate().toLocalDate().isBefore(dateDebut)))
                .filter(e -> dateFin == null || (e.getDate() != null && !e.getDate().toLocalDate().isAfter(dateFin)))
                .filter(e -> annee == null || (e.getDate() != null && e.getDate().getYear() == annee))
                .collect(Collectors.toList());
    }
    /**
     * Statistiques globales
     */
    public Map<String, Object> getStatistiquesGlobales() {
        Map<String, Object> stats = new HashMap<>();

        long locationsActives = contratsRepository.countByStatus(StatusContrat.ACTIF);
        long locationsTerminees = contratsRepository.countByStatus(StatusContrat.EXPIRE);

        Double revenusTotal = contratsRepository.findAll().stream()
                .filter(c -> c.getLoyerMensuel() != null)
                .mapToDouble(Contrats::getLoyerMensuel)
                .sum();

        stats.put("locationsActives", locationsActives);
        stats.put("locationsTerminees", locationsTerminees);
        stats.put("revenusTotal", revenusTotal);

        return stats;
    }

  /**
     * Export Excel
     */
    public byte[] exportToExcel(Map<String, Object> filters) throws Exception {
        List<EvenementHistorique> historiques = getAllHistorique();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Historique");
            String[] headers = {"ID", "Type", "Titre", "Date", "Locataire", "Bien", "Montant", "Statut", "Description"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (EvenementHistorique h : historiques) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(Optional.ofNullable(h.getId()).orElse(0L));
                row.createCell(1).setCellValue(Optional.ofNullable(h.getType()).orElse(""));
                row.createCell(2).setCellValue(Optional.ofNullable(h.getTitre()).orElse(""));
                row.createCell(3).setCellValue(h.getDate() != null ? h.getDate().toString() : "");
                row.createCell(4).setCellValue(Optional.ofNullable(h.getLocataire()).orElse(""));
                row.createCell(5).setCellValue(Optional.ofNullable(h.getBien()).orElse(""));
                row.createCell(6).setCellValue(Optional.ofNullable(h.getMontant()).orElse(0.0));
                row.createCell(7).setCellValue(Optional.ofNullable(h.getStatut()).orElse(""));
                row.createCell(8).setCellValue(Optional.ofNullable(h.getDescription()).orElse(""));
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }


    /**
     * Récupérer l'historique par utilisateur
     */
    public List<EvenementHistorique> getHistoriqueByUser(Long userId) {
        return getHistoriqueFiltre(userId, null, null, null, null, null, null);
    }

    /**
     * Récupérer l'historique par bien
     */
    public List<EvenementHistorique> getHistoriqueByBien(Long bienId) {
        return getHistoriqueFiltre(null, bienId, null, null, null, null, null);
    }
}
