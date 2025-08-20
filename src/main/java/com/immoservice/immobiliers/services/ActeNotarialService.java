package com.immoservice.immobiliers.services;

import com.immoservice.immobiliers.entities.ActeNotarial;
import com.immoservice.immobiliers.entities.TransactionImmobiliere;
import com.immoservice.immobiliers.repository.ActeNotarialRepository;
import com.immoservice.immobiliers.repository.TransactionImmobiliereRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActeNotarialService {

    private final ActeNotarialRepository acteNotarialRepository;
    private final TransactionImmobiliereRepository transactionImmobiliereRepository;

    public ActeNotarialService(ActeNotarialRepository acteNotarialRepository,
            TransactionImmobiliereRepository transactionImmobiliereRepository) {
        this.acteNotarialRepository = acteNotarialRepository;
        this.transactionImmobiliereRepository = transactionImmobiliereRepository;
    }

    public ActeNotarial creerActe(Long transactionId, ActeNotarial acte) {
        TransactionImmobiliere transaction = transactionImmobiliereRepository.findById(transactionId)
                .orElseThrow(
                        () -> new RuntimeException("Transaction immobilière introuvable avec ID : " + transactionId));

        acte.setTransaction(transaction);
        return acteNotarialRepository.save(acte);
    }

    public List<ActeNotarial> getTousLesActes() {
        return acteNotarialRepository.findAll();
    }

    public List<ActeNotarial> getActesParTransaction(Long transactionId) {
        return acteNotarialRepository.findByTransactionId(transactionId);
    }

    public ActeNotarial getActeParId(Long id) {
        return acteNotarialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Acte notarial introuvable avec ID : " + id));
    }

    public ActeNotarial mettreAJourActe(Long id, ActeNotarial acte) {
        ActeNotarial existing = getActeParId(id);
        existing.setNumeroActe(acte.getNumeroActe());
        existing.setDateSignature(acte.getDateSignature());
        return acteNotarialRepository.save(existing);
    }

    public void supprimerActe(Long id) {
        if (!acteNotarialRepository.existsById(id)) {
            throw new RuntimeException("Acte notarial introuvable avec ID : " + id);
        }
        acteNotarialRepository.deleteById(id);
    }
}
