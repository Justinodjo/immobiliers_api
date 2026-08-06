package com.immoservice.immobiliers.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.dto.MandatGestionDTO;
import com.immoservice.immobiliers.entities.Arrondissement;
import com.immoservice.immobiliers.entities.BiensImmobiliers;
import com.immoservice.immobiliers.entities.Commune;
import com.immoservice.immobiliers.entities.Departement;
import com.immoservice.immobiliers.entities.Quartier;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.ArrondissementRepository;
import com.immoservice.immobiliers.repository.BiensImmobiliersRepository;
import com.immoservice.immobiliers.repository.CommuneRepository;
import com.immoservice.immobiliers.repository.DepartementRepository;
import com.immoservice.immobiliers.repository.QuartierRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;

@Service
public class MandatGestionService {

    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private BiensImmobiliersRepository biensRepository;
    @Autowired
    private DepartementRepository departementRepository;
    @Autowired
    private CommuneRepository communeRepository;
    @Autowired
    private ArrondissementRepository arrondissementRepository;
    @Autowired
    private QuartierRepository quartierRepository;

    @Autowired
    private NotificationService notificationService;

    public int mandaterGestionnaire(MandatGestionDTO dto) {
        UserInfo proprietaire = userInfoRepository.findById(dto.getProprietaireId())
                .orElseThrow(() -> new RuntimeException("Propriétaire introuvable"));
        UserInfo gestionnaire = userInfoRepository.findById(dto.getGestionnaireId())
                .orElseThrow(() -> new RuntimeException("Gestionnaire introuvable"));
        // Récupération des biens concernés
        List<BiensImmobiliers> biens = findBiensInZone(dto, proprietaire);
        if (biens.isEmpty()) {
            throw new RuntimeException("Aucun bien trouvé pour ce propriétaire dans la zone spécifiée.");
        }

        // ✅ Réassignation des biens
        biens.forEach(b -> b.setUserInfo(gestionnaire));

        biensRepository.saveAll(biens);
        // 🔔 Notifications en temps réel
        String messageGestionnaire = String.format(
                "📄 Vous avez reçu un nouveau mandat de gestion de la part de %s (%d bien%s concerné%s).",
                proprietaire.getNomComplet(),
                biens.size(),
                biens.size() > 1 ? "s" : "",
                biens.size() > 1 ? "s" : "");
        notificationService.envoyerNotificationPrivee(gestionnaire, messageGestionnaire);

        String messageProprio = String.format(
                "✅ Votre mandat a bien été envoyé à %s pour la gestion de %d bien%s.",
                gestionnaire.getNomComplet(),
                biens.size(),
                biens.size() > 1 ? "s" : "");
        notificationService.envoyerNotificationPrivee(proprietaire, messageProprio);

        // 🧩 Notification orientée au staff système (optionnelle)
        String messageSystem = String.format(
                "🔔 Nouveau mandat : %s a mandaté %s pour %d bien%s.",
                proprietaire.getNomComplet(),
                gestionnaire.getNomComplet(),
                biens.size(),
                biens.size() > 1 ? "s" : "");
        notificationService.envoyerNotificationRestreinte("ADMIN", messageSystem);
        return biens.size();
    }

    public int simulerMandat(MandatGestionDTO dto) {
        UserInfo proprietaire = userInfoRepository.findById(dto.getProprietaireId())
                .orElseThrow(() -> new RuntimeException("Propriétaire introuvable"));

        List<BiensImmobiliers> biens = findBiensInZone(dto, proprietaire);

        return biens.size();
    }

    /**
     * 🧩 Méthode utilitaire pour récupérer les biens du propriétaire selon la zone
     * choisie.
     */
    private List<BiensImmobiliers> findBiensInZone(MandatGestionDTO dto, UserInfo proprietaire) {
        if (dto.getQuartierId() != null) {
            Quartier quartier = quartierRepository.findById(dto.getQuartierId())
                    .orElseThrow(() -> new RuntimeException("Quartier introuvable"));
            return biensRepository.findByUserInfoAndQuartier(proprietaire, quartier);
        } else if (dto.getArrondissementId() != null) {
            Arrondissement arr = arrondissementRepository.findById(dto.getArrondissementId())
                    .orElseThrow(() -> new RuntimeException("Arrondissement introuvable"));
            return biensRepository.findByUserInfoAndArrondissement(proprietaire, arr);
        } else if (dto.getCommuneId() != null) {
            Commune commune = communeRepository.findById(dto.getCommuneId())
                    .orElseThrow(() -> new RuntimeException("Commune introuvable"));
            return biensRepository.findByUserInfoAndCommune(proprietaire, commune);
        } else if (dto.getDepartementId() != null) {
            Departement dep = departementRepository.findById(dto.getDepartementId())
                    .orElseThrow(() -> new RuntimeException("Département introuvable"));
            return biensRepository.findByUserInfoAndDepartement(proprietaire, dep);
        }
        throw new RuntimeException("Aucune zone (quartier, arrondissement, commune ou département) spécifiée.");
    }

}
