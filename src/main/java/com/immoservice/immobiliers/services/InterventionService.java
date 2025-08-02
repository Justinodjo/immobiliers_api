package com.immoservice.immobiliers.services;

import com.immoservice.immobiliers.entities.Intervention;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.InterventionRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;
import com.immoservice.immobiliers.repository.BiensImmobiliersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InterventionService {

     @Autowired
    private final InterventionRepository interventionRepository;
    @Autowired
    private final BiensImmobiliersRepository biensImmobiliersRepository;
    @Autowired
    private final UserInfoRepository userInfoRepository;

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

        return interventionRepository.save(intervention);
    }

    @Transactional
    public Intervention update(Long id, Intervention updatedIntervention) {
        Intervention existing = interventionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Intervention non trouvée avec l'ID : " + id));

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

        return interventionRepository.save(existing);
    }

    public void delete(Long id) {
        if (!interventionRepository.existsById(id)) {
            throw new EntityNotFoundException("Intervention non trouvée avec l'ID : " + id);
        }
        interventionRepository.deleteById(id);
    }
}
