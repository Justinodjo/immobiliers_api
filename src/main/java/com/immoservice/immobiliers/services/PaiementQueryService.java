package com.immoservice.immobiliers.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.entities.Paiements;
import com.immoservice.immobiliers.repository.PaiementsRepository;

@Service
public class PaiementQueryService {

    @Autowired
    private PaiementsRepository paiementsRepository;

    public List<Paiements> parBien(Long bienId) {
        return paiementsRepository.findByContrats_BiensImmobiliers_Id(bienId);
    }

    public List<Paiements> parDetail(Long detailId) {
        return paiementsRepository.findByDetailsBienId(detailId);
    }

    public List<Paiements> parBienOuDetail(Long bienId) {
        Set<Paiements> paiements = new HashSet<>(parBien(bienId));
        paiements.addAll(parDetail(bienId));
        return new ArrayList<>(paiements);
    }
}
