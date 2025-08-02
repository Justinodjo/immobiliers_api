package com.immoservice.immobiliers.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.entities.Arrondissement;
import com.immoservice.immobiliers.repository.ArrondissementRepository;

@Service
public class ArrondissementService {
    private ArrondissementRepository arrondissementRepository;

    public ArrondissementService(ArrondissementRepository arrondissementRepository) {
        this.arrondissementRepository = arrondissementRepository;
    }

    // Afficher la liste de tous les departements de la base de données
    public List<Arrondissement> getAllArrondissement() {
        return arrondissementRepository.findAll();
    }

    // Afficher les informations d'un departement spécifique
    public Arrondissement getArrondissementById(Long idArrondissement) {
        return arrondissementRepository.findById(idArrondissement).orElse(null);
    }

    // Créer un Arrondissement dans la base de données

    public Arrondissement createArrondissement(Arrondissement arrondissement) {
        return arrondissementRepository.save(arrondissement);
    }

    // Supprimer un departement de la base de données
    public void deleteArrondissement(Long idArrondissement) {
        arrondissementRepository.deleteById(idArrondissement);
    }

    // public List<Arrondissement> getArrondissementsByCommuneId(Long idCommune) {
    // // Requête pour récupérer les communes par département (via repository)
    // return arrondissementRepository.findByCommune_IdCommune(idCommune);
    // }

    public List<Arrondissement> getArrondissementsByNomCommunes(String libelle) {
        return arrondissementRepository.findByCommune_Libelle(libelle);
    }
}
