package com.immoservice.immobiliers.services;

import com.immoservice.immobiliers.entities.TransactionImmobiliere;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.TransactionImmobiliereRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TransactionImmobiliereService {

    private final TransactionImmobiliereRepository transactionRepository;
    private final UserInfoRepository userInfoRepository;

    public TransactionImmobiliereService(TransactionImmobiliereRepository transactionRepository,
            UserInfoRepository userInfoRepository) {
        this.transactionRepository = transactionRepository;
        this.userInfoRepository = userInfoRepository;
    }

    public TransactionImmobiliere creerTransaction(TransactionImmobiliere transaction, Set<Integer> userIds) {
        Set<UserInfo> parties = new HashSet<>(userInfoRepository.findAllById(userIds));
        transaction.setParties(parties);
        return transactionRepository.save(transaction);
    }

    public List<TransactionImmobiliere> getToutesTransactions() {
        return transactionRepository.findAll();
    }

    public TransactionImmobiliere getTransactionParId(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction immobilière introuvable avec ID : " + id));
    }

    public List<TransactionImmobiliere> getTransactionsParType(String typeTransaction) {
        return transactionRepository.findByTypeTransaction(typeTransaction);
    }

    public TransactionImmobiliere mettreAJourTransaction(Long id, TransactionImmobiliere transaction,
            Set<Integer> userIds) {
        TransactionImmobiliere existante = getTransactionParId(id);
        existante.setTypeTransaction(transaction.getTypeTransaction());

        if (userIds != null && !userIds.isEmpty()) {
            Set<UserInfo> parties = new HashSet<>(userInfoRepository.findAllById(userIds));
            existante.setParties(parties);
        }

        return transactionRepository.save(existante);
    }

    public void supprimerTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction immobilière introuvable avec ID : " + id);
        }
        transactionRepository.deleteById(id);
    }
}
