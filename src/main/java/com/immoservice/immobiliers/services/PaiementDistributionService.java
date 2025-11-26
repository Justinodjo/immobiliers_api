package com.immoservice.immobiliers.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.entities.Contrats;
import com.immoservice.immobiliers.entities.PaiementDistribution;
import com.immoservice.immobiliers.entities.Paiements;
import com.immoservice.immobiliers.entities.StatutPaiement;
import com.immoservice.immobiliers.repository.PaiementDistributionRepository;

import jakarta.transaction.Transactional;

@Service
public class PaiementDistributionService {

    @Autowired
    private PaiementDistributionRepository distributionRepository;

    @Transactional
    public void distribuerPaiement(Paiements paiement) {
        Contrats contrat = paiement.getContrats();

        if (contrat == null) {
            throw new IllegalArgumentException("Le paiement n’est pas associé à un contrat.");
        }

        // --- Définition des taux de commission ---
        double tauxCommissionSysteme = contrat.getCommissionSysteme() != null
                ? contrat.getCommissionSysteme() / 100
                : 0.05;

        double tauxCommissionGestionnaire = contrat.getCommissionGestionnaire() != null
                ? contrat.getCommissionGestionnaire() / 100
                : 0.10;

        double montantBrut = paiement.getMontant();

        double commissionSysteme = montantBrut * tauxCommissionSysteme;
        double commissionGestionnaire = montantBrut * tauxCommissionGestionnaire;
        double montantNetProprietaire = montantBrut - commissionSysteme - commissionGestionnaire;
        double montantNetGestionnaire = commissionGestionnaire; // Si gestionnaire distinct

        PaiementDistribution distribution = new PaiementDistribution();
        distribution.setPaiement(paiement);
        distribution.setContrat(contrat);
        distribution.setMontantBrut(montantBrut);
        distribution.setCommissionSysteme(commissionSysteme);
        distribution.setCommissionGestionnaire(commissionGestionnaire);
        distribution.setMontantNetProprietaire(montantNetProprietaire);
        distribution.setMontantNetGestionnaire(montantNetGestionnaire);
        distribution.setBeneficiaireProprietaire(contrat.getUserInfo());
        distribution.setBeneficiaireGestionnaire(contrat.getGestionnaire());

        distributionRepository.save(distribution);
        paiement.getDistributions().add(distribution);

        // Optionnel : marquer le paiement comme distribué
        paiement.setStatut(StatutPaiement.PAYE);
        System.out.printf(
                "✅ Distribution réussie pour le paiement #%d : %.2f€ au propriétaire, %.2f€ au gestionnaire%n",
                paiement.getId(), montantNetProprietaire, montantNetGestionnaire);
    }
}
