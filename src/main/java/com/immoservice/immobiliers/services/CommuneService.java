package com.immoservice.immobiliers.services;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.immoservice.immobiliers.entities.Departement;
import com.immoservice.immobiliers.entities.Commune;
import com.immoservice.immobiliers.repository.CommuneRepository;
import com.immoservice.immobiliers.repository.DepartementRepository;

/**
 * CommuneService
 */
@Service
public class CommuneService {
    private static final Logger logger = LoggerFactory.getLogger(CommuneService.class);
    private final CommuneRepository communeRepository;
    private final DepartementRepository departementRepository;

    public CommuneService(CommuneRepository communeRepository, DepartementRepository departementRepository) {
        this.communeRepository = communeRepository;
        this.departementRepository = departementRepository;
    }

    // Afficher la liste de tous les departements de la base de données
    public List<Commune> getAllCommunes() {
        return communeRepository.findAll();
    }

    // Afficher les informations d'un departement spécifique
    public Commune getCommuneById(Long idCommune) {
        return communeRepository.findById(idCommune).orElse(null);
    }

    // Créer un departement dans la base de données

    public Commune createCommune(Commune commune) {
        return communeRepository.save(commune);
    }

    // Supprimer un departement de la base de données
    public void deleteCommune(Long idCommune) {
        communeRepository.deleteById(idCommune);
    }

   public List<Commune> getCommunesByDepartement(Long departementId) {
    Departement departement = departementRepository.findById(departementId)
            .orElseThrow(() -> new RuntimeException("Département non trouvé avec ID: " + departementId));
    return communeRepository.findByDepartement(departement);
}

    // Récupérer les communes par nom de département
    public List<Commune> getCommunesByNomDepartement(String libelle) {
        return communeRepository.findByDepartement_Libelle(libelle);
    }
}
