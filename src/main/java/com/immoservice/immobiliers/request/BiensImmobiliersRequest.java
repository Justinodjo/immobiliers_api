package com.immoservice.immobiliers.request;

public class BiensImmobiliersRequest {
    private String nom;
    private String adresse;
    private Double latitude;
    private Double longitude;
    private Boolean status;
    private Boolean aVendre;
    private Double superficie;
    private int prix;
    private String description;
    private Long typeBiensImmobiliereId;
    private Long userInfoId;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getaVendre() {
        return aVendre;
    }

    public void setaVendre(Boolean aVendre) {
        this.aVendre = aVendre;
    }

    public Double getSuperficie() {
        return superficie;
    }

    public void setSuperficie(Double superficie) {
        this.superficie = superficie;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTypeBiensImmobiliereId() {
        return typeBiensImmobiliereId;
    }

    public void setTypeBiensImmobiliereId(Long typeBiensImmobiliereId) {
        this.typeBiensImmobiliereId = typeBiensImmobiliereId;
    }

    public Long getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(Long userInfoId) {
        this.userInfoId = userInfoId;
    }
}
