package com.immoservice.immobiliers.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.entities.Departement;
import com.immoservice.immobiliers.repository.DepartementRepository;
import com.immoservice.immobiliers.repository.CommuneRepository;

@Service
public class DepartementService {
    private DepartementRepository departementRepository;
    private CommuneRepository communeRepository;

    public DepartementService(DepartementRepository departementRepository, CommuneRepository communeRepository) {
        this.departementRepository = departementRepository;
        this.communeRepository = communeRepository;
    }

    // Afficher la liste de tous les departements de la base de données
    public List<Departement> getAllDepartements() {
        return departementRepository.findAll();
    }

    // Afficher les informations d'un departement spécifique
    public Departement getDepartementById(Long idDepartement) {
        return departementRepository.findById(idDepartement).orElse(null);
    }

    // Créer un departement dans la base de données

    public Departement createDepartement(Departement departement) {
        return departementRepository.save(departement);
    }

    // Supprimer un departement de la base de données
    public void deleteDepartement(Long idDepartement) {
        departementRepository.deleteById(idDepartement);
    }

    // Méthode ajoutée pour obtenir les communes d'un département
    /*
     * public List<Commune> getCommunesByDepartementId(Long idDepartement) {
     * Departement departement =
     * departementRepository.findById(idDepartement).orElse(null);
     * if (departement != null) {
     * return departement.getCommunes();
     * }
     * return Collections.emptyList();
     * }
     */
}
