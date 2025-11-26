package com.immoservice.immobiliers.dto;

import java.util.List;

public class GestionnaireDTO {

    private Integer id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private Long fichierId;

    // Statistiques
    private int nbContratsGeres;
    private double revenuGenere;
    private int score;
    private double tauxReussite;
    private boolean disponible;

    // Informations supplémentaires
    private String specialite;
    private List<String> zonesGestion;

    // ---- Constructeur vide ----
    public GestionnaireDTO() {
    }

    // ---- Constructeur complet ----
    public GestionnaireDTO(Integer id, String nom, String prenom, String email, String telephone, String adresse,
            Long fichierId, int nbContratsGeres, double revenuGenere, int score,
            double tauxReussite, boolean disponible, String specialite, List<String> zonesGestion) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
        this.fichierId = fichierId;
        this.nbContratsGeres = nbContratsGeres;
        this.revenuGenere = revenuGenere;
        this.score = score;
        this.tauxReussite = tauxReussite;
        this.disponible = disponible;
        this.specialite = specialite;
        this.zonesGestion = zonesGestion;
    }

    // ---- Getters & Setters ----

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Long getFichierId() {
        return fichierId;
    }

    public void setFichierId(Long fichierId) {
        this.fichierId = fichierId;
    }

    public int getNbContratsGeres() {
        return nbContratsGeres;
    }

    public void setNbContratsGeres(int nbContratsGeres) {
        this.nbContratsGeres = nbContratsGeres;
    }

    public double getRevenuGenere() {
        return revenuGenere;
    }

    public void setRevenuGenere(double revenuGenere) {
        this.revenuGenere = revenuGenere;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public double getTauxReussite() {
        return tauxReussite;
    }

    public void setTauxReussite(double tauxReussite) {
        this.tauxReussite = tauxReussite;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public List<String> getZonesGestion() {
        return zonesGestion;
    }

    public void setZonesGestion(List<String> zonesGestion) {
        this.zonesGestion = zonesGestion;
    }
}
