package com.immoservice.immobiliers.services;

import java.util.List;

import org.springframework.stereotype.Service;
import com.immoservice.immobiliers.entities.Commune;
import com.immoservice.immobiliers.repository.CommuneRepository;
import com.immoservice.immobiliers.repository.DepartementRepository;

/**
 * CommuneService
 */
@Service
public class CommuneService {
    private CommuneRepository communeRepository;
    @SuppressWarnings("unused")
    private DepartementRepository departementRepository;

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

    // public List<Commune> getCommunesByDepartementId(Long idDepartement) {
    // // Requête pour récupérer les communes par département (via repository)
    // return communeRepository.findByDepartement_IdDepartement(idDepartement);
    // }

    // Récupérer les communes par nom de département
    public List<Commune> getCommunesByNomDepartement(String libelle) {
        return communeRepository.findByDepartement_Libelle(libelle);
    }
}
