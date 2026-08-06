package com.immoservice.immobiliers.services;
import com.immoservice.immobiliers.dto.DetailDTO;
import com.immoservice.immobiliers.dto.ContratDTO;
import com.immoservice.immobiliers.dto.BienDTO;
import com.immoservice.immobiliers.entities.Contrats;
import com.immoservice.immobiliers.repository.ContratsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContratValidationService {

    @Autowired
    private ContratsRepository contratsRepository;

    /**
     * Vérifie si un bien est déjà sous contrat actif
     */
    public ValidationResult validateBienDisponibilite(Long bienId, LocalDate dateDebut, LocalDate dateFin) {
        List<Contrats> contratsActifs = contratsRepository.findContratsByBienAndDateRange(
                bienId,
                dateDebut,
                dateFin);

        if (!contratsActifs.isEmpty()) {
            return ValidationResult.error(
                    "BIEN_DEJA_LOUE",
                    String.format("Le bien est déjà sous contrat du %s au %s",
                            contratsActifs.get(0).getDateDebut(),
                            contratsActifs.get(0).getDateFin()));
        }

        return ValidationResult.success();
    }

    /**
     * Vérifie si un détail (chambre) est déjà sous contrat actif
     */
    public ValidationResult validateDetailDisponibilite(Long detailId, LocalDate dateDebut, LocalDate dateFin) {
        List<Contrats> contratsActifs = contratsRepository.findContratsByDetailAndDateRange(detailId,dateDebut,dateFin);

        if (!contratsActifs.isEmpty()) {
            return ValidationResult.error(
                    "DETAIL_DEJA_LOUE",
                    String.format("Cette chambre est déjà sous contrat du %s au %s",
                            contratsActifs.get(0).getDateDebut(),
                            contratsActifs.get(0).getDateFin()));
        }

        return ValidationResult.success();
    }

    /**
     * Validation complète d'un contrat avant création
     */
    @Transactional(readOnly = true)
    public ValidationResult validateContrat(ContratDTO contratDTO) {
        List<String> errors = new ArrayList<>();
        
        // 1. Vérifier que les dates sont cohérentes
        if (contratDTO.getDateDebut().isAfter(contratDTO.getDateFin())) {
            errors.add("La date de début doit être antérieure à la date de fin");
        }
        
        // 2. Vérifier que la date de début n'est pas dans le passé
        if (contratDTO.getDateDebut().isBefore(LocalDate.now())) {
            errors.add("La date de début ne peut pas être dans le passé");
        }
        
        // 3. Vérifier la disponibilité des biens
        if (contratDTO.getBiensImmobiliers() != null) {
            for (BienDTO bien : contratDTO.getBiensImmobiliers()) {
                ValidationResult result = validateBienDisponibilite(
                    bien.getId(), 
                    contratDTO.getDateDebut(), 
                    contratDTO.getDateFin()
                );
                
                if (!result.isValid()) {
                    errors.add("Bien ID " + bien.getId() + ": " + result.getMessage());
                }
            }
        }
        
        // 4. Vérifier la disponibilité des détails (chambres)
        if (contratDTO.getDetailsBiensImmobiliers() != null) {
            for (DetailDTO detail : contratDTO.getDetailsBiensImmobiliers()) {
                ValidationResult result = validateDetailDisponibilite(
                    detail.getId(), 
                    contratDTO.getDateDebut(), 
                    contratDTO.getDateFin()
                );
                
                if (!result.isValid()) {
                    errors.add("Chambre ID " + detail.getId() + ": " + result.getMessage());
                }
            }
        }
        
        // 5. Vérifier qu'au moins un bien ou détail est sélectionné
        boolean hasBiens = contratDTO.getBiensImmobiliers() != null && 
                          !contratDTO.getBiensImmobiliers().isEmpty();
        boolean hasDetails = contratDTO.getDetailsBiensImmobiliers() != null && 
                            !contratDTO.getDetailsBiensImmobiliers().isEmpty();
        
        if (!hasBiens && !hasDetails) {
            errors.add("Aucun bien ou chambre sélectionné");
        }
        
        // 6. Vérifier le loyer
        if (contratDTO.getLoyerMensuel() == null || contratDTO.getLoyerMensuel() <= 0) {
            errors.add("Le loyer mensuel doit être supérieur à 0");
        }
        
        // 7. Vérifier que l'utilisateur existe
        if (contratDTO.getUserId() == null) {
            errors.add("Utilisateur non spécifié");
        }
        
        if (errors.isEmpty()) {
            return ValidationResult.success();
        } else {
            return ValidationResult.error("VALIDATION_FAILED", String.join("; ", errors));
        }
    }

    public static class ValidationResult {
    private boolean valid;
    private String code;
    private String message;
    private Map<String, Object> data;

    private ValidationResult(boolean valid, String code, String message) {
        this.valid = valid;
        this.code = code;
        this.message = message;
        this.data = new HashMap<>();
    }

    public static ValidationResult success() {
        return new ValidationResult(true, "SUCCESS", "Validation réussie");
    }

    public static ValidationResult error(String code, String message) {
        return new ValidationResult(false, code, message);
    }

    public boolean isValid() {
        return valid;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public ValidationResult withData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
}