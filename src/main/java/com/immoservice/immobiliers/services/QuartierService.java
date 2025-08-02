package com.immoservice.immobiliers.services;

import java.util.List;

import org.springframework.stereotype.Service;
import com.immoservice.immobiliers.entities.Quartier;
import com.immoservice.immobiliers.repository.QuartierRepository;

@Service
public class QuartierService {
    private QuartierRepository quartierRepository;

    public QuartierService(QuartierRepository quartierRepository) {
        this.quartierRepository = quartierRepository;
    }

    // Afficher la liste de tous les departements de la base de données
    public List<Quartier> getAllQuartiers() {
        return quartierRepository.findAll();
    }

    // Afficher les informations d'un departement spécifique
    public Quartier getQuartierById(Long idQartier) {
        return quartierRepository.findById(idQartier).orElse(null);
    }

    // Créer un departement dans la base de données

    public Quartier createQuartier(Quartier quartier) {
        return quartierRepository.save(quartier);
    }

    // Supprimer un departement de la base de données
    public void deleteQuartier(Long idQartier) {
        quartierRepository.deleteById(idQartier);
    }
}