package com.immoservice.immobiliers.entities;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Visite360 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Column(length = 2000)
    private String urlEmbed; // URL iframe

    private String plateforme; // "matterport", "kuula", "cloudpano"

    @ManyToOne
    @JoinColumn(name = "bien_id")
    private BiensImmobiliers bien;

    private String thumbnailUrl;

    private Integer nombreVues;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    private Boolean vrSupport; // Support casque VR

    @ElementCollection
    private List<String> hotspots; // Points d'intérêt

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getUrlEmbed() {
        return urlEmbed;
    }

    public void setUrlEmbed(String urlEmbed) {
        this.urlEmbed = urlEmbed;
    }

    public String getPlateforme() {
        return plateforme;
    }

    public void setPlateforme(String plateforme) {
        this.plateforme = plateforme;
    }

    public BiensImmobiliers getBien() {
        return bien;
    }

    public void setBien(BiensImmobiliers bien) {
        this.bien = bien;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Integer getNombreVues() {
        return nombreVues;
    }

    public void setNombreVues(Integer nombreVues) {
        this.nombreVues = nombreVues;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Boolean getVrSupport() {
        return vrSupport;
    }

    public void setVrSupport(Boolean vrSupport) {
        this.vrSupport = vrSupport;
    }

    public List<String> getHotspots() {
        return hotspots;
    }

    public void setHotspots(List<String> hotspots) {
        this.hotspots = hotspots;
    }

}