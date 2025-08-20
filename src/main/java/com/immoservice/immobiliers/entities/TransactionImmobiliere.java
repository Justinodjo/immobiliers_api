package com.immoservice.immobiliers.entities;

import java.util.Set;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class TransactionImmobiliere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String typeTransaction; // Vente, Location, etc.

    @ManyToMany
    @JoinTable(name = "transaction_utilisateurs", joinColumns = @JoinColumn(name = "transaction_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserInfo> parties;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private Set<ActeNotarial> actes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeTransaction() {
        return typeTransaction;
    }

    public void setTypeTransaction(String typeTransaction) {
        this.typeTransaction = typeTransaction;
    }

    public Set<UserInfo> getParties() {
        return parties;
    }

    public void setParties(Set<UserInfo> parties) {
        this.parties = parties;
    }

    public Set<ActeNotarial> getActes() {
        return actes;
    }

    public void setActes(Set<ActeNotarial> actes) {
        this.actes = actes;
    }
}
