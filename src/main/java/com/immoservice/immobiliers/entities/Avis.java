package com.immoservice.immobiliers.entities;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Avis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom; // Exemple : "Jacqueline Miller"
    private LocalDate dateSejour; // Exemple : 2022-11-13
    private int nombreAvis; // Exemple : 4 avis rédigés
    private double note; // Exemple : 4.5
    @Column(length = 2000)
    private String commentaire; // Texte de l'avis

    private String role; // Exemple : "Directeur" si applicable
    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    private Fichier fichier;
    @OneToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "Avis_id")
    private List<Fichier> fichiers;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "user_info_id") // nom de la colonne FK dans la table Avis
    private UserInfo userInfo;
    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "biensImmobiliers_id")
    private BiensImmobiliers biensImmobiliers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public LocalDate getDateSejour() {
        return dateSejour;
    }

    public void setDateSejour(LocalDate dateSejour) {
        this.dateSejour = dateSejour;
    }

    public int getNombreAvis() {
        return nombreAvis;
    }

    public void setNombreAvis(int nombreAvis) {
        this.nombreAvis = nombreAvis;
    }

    public double getNote() {
        return note;
    }

    public void setNote(double note) {
        this.note = note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Fichier getFichier() {
        return fichier;
    }

    public void setFichier(Fichier fichier) {
        this.fichier = fichier;
    }

    public List<Fichier> getFichiers() {
        return fichiers;
    }

    public void setFichiers(List<Fichier> fichiers) {
        this.fichiers = fichiers;
    }

    public BiensImmobiliers getBiensImmobiliers() {
        return biensImmobiliers;
    }

    public void setBiensImmobiliers(BiensImmobiliers biensImmobiliers) {
        this.biensImmobiliers = biensImmobiliers;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
