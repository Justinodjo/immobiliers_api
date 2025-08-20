package com.immoservice.immobiliers.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ActeNotarial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroActe;
    private java.time.LocalDate dateSignature;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private TransactionImmobiliere transaction;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroActe() {
        return numeroActe;
    }

    public void setNumeroActe(String numeroActe) {
        this.numeroActe = numeroActe;
    }

    public java.time.LocalDate getDateSignature() {
        return dateSignature;
    }

    public void setDateSignature(java.time.LocalDate dateSignature) {
        this.dateSignature = dateSignature;
    }

    public TransactionImmobiliere getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionImmobiliere transaction) {
        this.transaction = transaction;
    }

}
