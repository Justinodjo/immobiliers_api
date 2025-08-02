package com.immoservice.immobiliers.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.immoservice.immobiliers.entities.BiensImmobiliers;
import com.immoservice.immobiliers.entities.Contrats;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.BiensImmobiliersRepository;
import com.immoservice.immobiliers.repository.ContratsRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ContratsService {
    @Autowired
    private BiensImmobiliersRepository biensImmobiliersRepository;

    @Autowired
    private ContratsRepository contratsRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Transactional
    public Contrats saveContrat(Contrats contrats, String emailUtilisateur) {
        // Log des informations reçues avant l'enregistrement
        System.out.println(">>> Données reçues : " + contrats);
        System.out.println(">>> TypeServices avant calcul : " + contrats.getBiensImmobiliers());
        // Récupération de l'utilisateur connecté
        UserInfo utilisateur = null;
        if (contrats.getUserInfo() != null && contrats.getUserInfo().getId() != null) {
            utilisateur = userInfoRepository.findById(contrats.getUserInfo().getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Utilisateur non trouvé avec l'ID : " + contrats.getUserInfo().getId()));
        } else if (emailUtilisateur != null) {
            utilisateur = userInfoRepository.findByEmail(emailUtilisateur)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Utilisateur non trouvé avec l'email : " + emailUtilisateur));
        } else {
            throw new IllegalArgumentException("Aucun utilisateur associé au contrat.");
        }

        // Associer l'utilisateur à la réservation
        contrats.setUserInfo(utilisateur);
        // Vérifie et associe les services à la réservation
        fetchAndSetBiensImmobiliers(contrats);
        // Calcul du prix total
        contrats.setPrixTotal(contrats.calculatePrixTotal());

        // Sauvegarder la réservation
        Contrats savedContrat = contratsRepository.save(contrats);

        // Mise à jour de la note dynamique du bien associé
        BiensImmobiliers bien = contrats.getBiensImmobiliers().get(0); // ou fetch via bienId si besoin

        int nombreContrats = contratsRepository.countByBiensImmobiliers_Id(bien.getId()); // créez cette méthode dans le
                                                                                          // repo

        // Exemple de note : 2 pts par contrat jusqu’à 5 max
        double note = Math.min(nombreContrats * 2.0, 5.0);

        bien.setNote(note);
        biensImmobiliersRepository.save(bien);
        // Log après l'enregistrement
        System.out.println(">>> contrat après enregistrement : " + savedContrat);
        System.out.println(">>> Biens immobiliers après enregistrement : " + savedContrat.getBiensImmobiliers());

        // Retourner la réservation enregistrée
        return savedContrat;
    }

    private void fetchAndSetBiensImmobiliers(Contrats contrats) {
        // Vérifier si les services sont fournis
        if (contrats.getBiensImmobiliers() == null || contrats.getBiensImmobiliers().isEmpty()) {
            throw new IllegalArgumentException("Aucun bien fourni.");
        }

        // Récupérer les IDs des services
        List<Long> biensImmobiliersIds = contrats.getBiensImmobiliers().stream()
                .map(BiensImmobiliers::getId)
                .collect(Collectors.toList());

        // Chercher les services correspondants
        List<BiensImmobiliers> biens = biensImmobiliersRepository.findAllById(biensImmobiliersIds);

        // Vérifier si tous les services sont trouvés
        if (biens.size() != biensImmobiliersIds.size()) {
            throw new EntityNotFoundException("Un ou plusieurs Biens immobiliers fournis ne sont pas valides.");
        }

        // Log des services récupérés
        System.out.println(">>> Biens validés pour le contrat : " + biens);

        // Les services sont déjà valides, on ne change rien dans la réservation
        contrats.setBiensImmobiliers(biens);
    }

}
