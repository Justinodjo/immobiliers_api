package com.immoservice.immobiliers.services;

import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.immoservice.immobiliers.entities.StatutIntervention;
import com.immoservice.immobiliers.entities.Intervention;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.BiensImmobiliersRepository;
import com.immoservice.immobiliers.repository.InterventionRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class InterventionService {
  private static final Logger logger = LoggerFactory.getLogger(InterventionService.class);
    @Autowired
    private final InterventionRepository interventionRepository;
    @Autowired
    private final BiensImmobiliersRepository biensImmobiliersRepository;
    @Autowired
    private final UserInfoRepository userInfoRepository;

     @Autowired
    private NotificationService notificationService;

    public InterventionService(
            InterventionRepository interventionRepository,
            BiensImmobiliersRepository biensImmobiliersRepository,
            UserInfoRepository userInfoRepository) {
        this.interventionRepository = interventionRepository;
        this.biensImmobiliersRepository = biensImmobiliersRepository;
        this.userInfoRepository = userInfoRepository;
    }

    public List<Intervention> findAll() {
        return interventionRepository.findAll();
    }

    public Optional<Intervention> findById(Long id) {
        return interventionRepository.findById(id);
    }

    @Transactional
    public Intervention save(Intervention intervention) {
        // Calcule l'écart budgétaire si nécessaire
        if (intervention.getEstimationCout() != null && intervention.getCoutReel() != null) {
            intervention.setEcartBudgetaire(intervention.getCoutReel().subtract(intervention.getEstimationCout()));
        }

        // Vérifie si l'utilisateur existe (createdBy)
        if (intervention.getCreatedBy() != null && intervention.getCreatedBy().getId() != null) {
            UserInfo createdBy = userInfoRepository.findById(intervention.getCreatedBy().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur (createdBy) introuvable"));
            intervention.setCreatedBy(createdBy);
        }

        // Vérifie si l'utilisateur existe (updatedBy)
        if (intervention.getUpdatedBy() != null && intervention.getUpdatedBy().getId() != null) {
            UserInfo updatedBy = userInfoRepository.findById(intervention.getUpdatedBy().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur (updatedBy) introuvable"));
            intervention.setUpdatedBy(updatedBy);
        }
Intervention saved = interventionRepository.save(intervention);
         // Notifier le propriétaire du bien
        if (saved.getBiensImmobilers() != null && saved.getBiensImmobilers().getUserInfo() != null) {
            String message = "Une nouvelle intervention a été signalée pour votre bien : " + 
                           saved.getBiensImmobilers().getNom();
            notificationService.envoyerNotificationPrivee(
                saved.getBiensImmobilers().getUserInfo(), 
                message
            );
        }
logger.info("Intervention créée avec succès : {}", saved.getId());
        return saved;
    }

    @Transactional
    public Intervention update(Long id, Intervention updatedIntervention) {
        Intervention existing = interventionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Intervention non trouvée avec l'ID : " + id));
  StatutIntervention ancienStatut = existing.getStatut();
        existing.setTypeIntervention(updatedIntervention.getTypeIntervention());
        existing.setDescription(updatedIntervention.getDescription());
        existing.setDateSignalement(updatedIntervention.getDateSignalement());
        existing.setDateTraitement(updatedIntervention.getDateTraitement());
        existing.setStatut(updatedIntervention.getStatut());
        existing.setPriorite(updatedIntervention.getPriorite());
        existing.setEstimationCout(updatedIntervention.getEstimationCout());
        existing.setCoutReel(updatedIntervention.getCoutReel());
        existing.setDocuments(updatedIntervention.getDocuments());
        existing.setBiensImmobilers(updatedIntervention.getBiensImmobilers());
        existing.setUpdatedBy(updatedIntervention.getUpdatedBy());

        // Mise à jour de l'écart budgétaire
        if (existing.getEstimationCout() != null && existing.getCoutReel() != null) {
            existing.setEcartBudgetaire(existing.getCoutReel().subtract(existing.getEstimationCout()));
        }

     // Si le statut change vers "TERMINE", définir la date de traitement
        if (updatedIntervention.getStatut() == StatutIntervention.TERMINEE && existing.getDateTraitement() == null) {
            existing.setDateTraitement(new Date());
        }
Intervention updated = interventionRepository.save(existing);
 
        // 🔔 Notification si le statut change
        if (existing.getBiensImmobilers() != null && existing.getBiensImmobilers().getUserInfo() != null
                && ancienStatut != updated.getStatut()) {
            String message = String.format(
                    "Le statut de l'intervention pour %s est passé de %s à %s",
                    existing.getBiensImmobilers().getNom(),
                    ancienStatut,
                    updated.getStatut());
            notificationService.envoyerNotificationPrivee(
                    existing.getBiensImmobilers().getUserInfo(),
                    message);
        }

        return updated;
    }

    public void delete(Long id) {
        if (!interventionRepository.existsById(id)) {
            throw new EntityNotFoundException("Intervention non trouvée avec l'ID : " + id);
        }
        interventionRepository.deleteById(id);
    }
@Transactional(readOnly = true)
public Intervention getInterventionById(Long id) {
    return interventionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Intervention non trouvée avec l'ID : " + id));
}
      /**
     * Récupérer les interventions par bien immobilier
     */
    @Transactional
    public List<Intervention> getInterventionsByBien(Long bienId) {
        return interventionRepository.findByBiensImmobilersId(bienId);
    }

    /**
     * Récupérer les interventions par statut
     */
    @Transactional
    public List<Intervention> getInterventionsByStatut(StatutIntervention statut) {
        return interventionRepository.findByStatut(statut);
    }

    /**
     * Récupérer les interventions en attente
     */
    @Transactional
    public List<Intervention> getInterventionsEnAttente() {
        return interventionRepository.findInterventionsEnAttente();
    }

    /**
     * Récupérer les interventions en cours
     */
    @Transactional
    public List<Intervention> getInterventionsEnCours() {
        return interventionRepository.findInterventionsEnCours();
    }


    /**
     *🟢 Changer le statut d'une intervention
     */
    @Transactional
    public Intervention changerStatut(Long id, StatutIntervention nouveauStatut) {
        Intervention intervention = getInterventionById(id);
        StatutIntervention ancienStatut = intervention.getStatut();

        intervention.setStatut(nouveauStatut);

        if (nouveauStatut == StatutIntervention.TERMINEE && intervention.getDateTraitement() == null) {
            intervention.setDateTraitement(new Date());
        }

        Intervention updated = interventionRepository.save(intervention);

        // 🔔 Notification
        if (intervention.getBiensImmobilers() != null && intervention.getBiensImmobilers().getUserInfo() != null) {
            String message = String.format(
                    "Le statut de l'intervention pour %s est passé de %s à %s",
                    intervention.getBiensImmobilers().getNom(),
                    ancienStatut,
                    nouveauStatut);
            notificationService.envoyerNotificationPrivee(
                    intervention.getBiensImmobilers().getUserInfo(),
                    message);
        }

        logger.info("✅ Statut de l'intervention {} changé de {} à {}", id, ancienStatut, nouveauStatut);
        return updated;
    }

    /**
     * Récupérer les statistiques des interventions
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getStatistiques() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalInterventions = interventionRepository.count();
        long enAttente = interventionRepository.countByStatut(StatutIntervention.EN_ATTENTE);
        long enCours = interventionRepository.countByStatut(StatutIntervention.ENCOURS);
        long terminees = interventionRepository.countByStatut(StatutIntervention.TERMINEE);
        long planifie = interventionRepository.countByStatut(StatutIntervention.PLANIFIE);
        stats.put("total", totalInterventions);
        stats.put("enAttente", enAttente);
        stats.put("enCours", enCours);
        stats.put("terminees", terminees);
        stats.put("planifie", planifie);

        return stats;
    }

     /**
     * Récupérer les interventions par type
     */
    @Transactional(readOnly = true)
    public List<Intervention> getInterventionsByType(String type) {
        return interventionRepository.findByTypeIntervention(type);
    }
}
