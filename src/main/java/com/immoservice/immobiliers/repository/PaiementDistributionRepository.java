package com.immoservice.immobiliers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.Contrats;
import com.immoservice.immobiliers.entities.PaiementDistribution;
import com.immoservice.immobiliers.entities.Paiements;

@Repository
public interface PaiementDistributionRepository extends JpaRepository<PaiementDistribution, Long> {
    List<PaiementDistribution> findByPaiement(Paiements paiement);

    List<PaiementDistribution> findByContrat(Contrats contrat);
}
