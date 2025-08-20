package com.immoservice.immobiliers.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class NoteBien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int note; // entre 1 et 5

    @ManyToOne
    private BiensImmobiliers bien;

    @ManyToOne
    private UserInfo utilisateur;

    private LocalDateTime dateNoted = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public BiensImmobiliers getBien() {
        return bien;
    }

    public void setBien(BiensImmobiliers bien) {
        this.bien = bien;
    }

    public UserInfo getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(UserInfo utilisateur) {
        this.utilisateur = utilisateur;
    }

    public LocalDateTime getDateNoted() {
        return dateNoted;
    }

    public void setDateNoted(LocalDateTime dateNoted) {
        this.dateNoted = dateNoted;
    }
}
