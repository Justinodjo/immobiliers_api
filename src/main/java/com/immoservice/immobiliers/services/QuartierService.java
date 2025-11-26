package com.immoservice.immobiliers.services;

import java.util.List;

import org.springframework.stereotype.Service;
import com.immoservice.immobiliers.entities.Quartier;
import com.immoservice.immobiliers.repository.QuartierRepository;
import com.immoservice.immobiliers.repository.ArrondissementRepository;
import com.immoservice.immobiliers.entities.Arrondissement;
@Service
public class QuartierService {
       private final QuartierRepository quartierRepository;
    private final ArrondissementRepository arrondissementRepository;

    public QuartierService(QuartierRepository quartierRepository, ArrondissementRepository arrondissementRepository) {
        this.quartierRepository = quartierRepository;
        this.arrondissementRepository = arrondissementRepository;
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
public List<Quartier> getQuartiersByArrondissementId(Long idArrondissement) {
    Arrondissement arrondissement = arrondissementRepository.findById(idArrondissement)
        .orElseThrow(() -> new RuntimeException("Arrondissement non trouvé avec ID: " + idArrondissement));
    
    return quartierRepository.findByArrondissement(arrondissement);
}


}