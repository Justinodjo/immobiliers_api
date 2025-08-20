package com.immoservice.immobiliers.dto;

public class UserInfoBadges {
    private Integer id;
    private String nomComplet;
    private boolean estProprietaireVerifie;
    private boolean estLocataireSerieux;

    // constructeurs
    public UserInfoBadges(Integer id, String nomComplet, boolean estProprietaireVerifie, boolean estLocataireSerieux) {
        this.id = id;
        this.nomComplet = nomComplet;
        this.estProprietaireVerifie = estProprietaireVerifie;
        this.estLocataireSerieux = estLocataireSerieux;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }

    public boolean isEstProprietaireVerifie() {
        return estProprietaireVerifie;
    }

    public void setEstProprietaireVerifie(boolean estProprietaireVerifie) {
        this.estProprietaireVerifie = estProprietaireVerifie;
    }

    public boolean isEstLocataireSerieux() {
        return estLocataireSerieux;
    }

    public void setEstLocataireSerieux(boolean estLocataireSerieux) {
        this.estLocataireSerieux = estLocataireSerieux;
    }

    // Getters & Setters
}