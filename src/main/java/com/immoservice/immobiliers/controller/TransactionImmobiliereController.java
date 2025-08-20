package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.entities.TransactionImmobiliere;
import com.immoservice.immobiliers.services.TransactionImmobiliereService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/transactions")
public class TransactionImmobiliereController {

    private final TransactionImmobiliereService transactionService;

    public TransactionImmobiliereController(TransactionImmobiliereService transactionService) {
        this.transactionService = transactionService;
    }

    // Créer une transaction
    @PostMapping
    public ResponseEntity<TransactionImmobiliere> creerTransaction(
            @RequestBody TransactionImmobiliere transaction,
            @RequestParam Set<Integer> userIds) {
        return ResponseEntity.ok(transactionService.creerTransaction(transaction, userIds));
    }

    // Récupérer toutes les transactions
    @GetMapping
    public ResponseEntity<List<TransactionImmobiliere>> getToutesTransactions() {
        return ResponseEntity.ok(transactionService.getToutesTransactions());
    }

    // Récupérer une transaction par ID
    @GetMapping("/{id}")
    public ResponseEntity<TransactionImmobiliere> getTransactionParId(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionParId(id));
    }

    // Récupérer par type
    @GetMapping("/type/{typeTransaction}")
    public ResponseEntity<List<TransactionImmobiliere>> getTransactionsParType(@PathVariable String typeTransaction) {
        return ResponseEntity.ok(transactionService.getTransactionsParType(typeTransaction));
    }

    // Mettre à jour une transaction
    @PutMapping("/{id}")
    public ResponseEntity<TransactionImmobiliere> mettreAJourTransaction(
            @PathVariable Long id,
            @RequestBody TransactionImmobiliere transaction,
            @RequestParam(required = false) Set<Integer> userIds) {
        return ResponseEntity.ok(transactionService.mettreAJourTransaction(id, transaction, userIds));
    }

    // Supprimer une transaction
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerTransaction(@PathVariable Long id) {
        transactionService.supprimerTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
