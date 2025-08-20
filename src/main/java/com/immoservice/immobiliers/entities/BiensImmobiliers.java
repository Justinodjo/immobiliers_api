package com.immoservice.immobiliers.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.immoservice.immobiliers.config.AuditLogListener;
import com.immoservice.immobiliers.enums.TypeUnite;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
@EntityListeners(AuditLogListener.class)
public class BiensImmobiliers extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String adresse;
    @ManyToOne
    private Departement departement;
    @ManyToOne
    private Commune commune;
    @ManyToOne
    private Arrondissement arrondissement;
    private Double latitude;
    private Double longitude;
    private Boolean status; // actif, inactif
    @Column(name = "nombre_vues")
    private Integer nombreVues = 0;
    private Integer nombreJaime = 0;
    private Boolean aVendre;
    @Column(nullable = true)
    private Double note;
    private Double superficie;
    private Double prix;
    @Enumerated(EnumType.STRING)
    private TypeUnite typeUnite;
    @Column(columnDefinition = "TEXT")
    private String description;
    @ManyToOne
    @JoinColumn(name = "typeBiens_id")
    private TypeBiensImmobiliere typeBiensImmobiliere;

    @ManyToOne
    @JoinColumn(name = "details_biens_immobiliers")
    @JsonManagedReference("biensImmobiliers-detailsBiensImmobiliers")
    private DetailsBiensImmobiliers detailsBiensImmobiliers;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "user_info_id") // Clé étrangère pour lier UserInfo à Reservation
    private UserInfo userInfo;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    private Fichier fichier;
    @OneToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "biens_immobiliers_id")
    private List<Fichier> fichiers;

    @CreatedBy
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "created_by_id") // Clé étrangère dans la table produits
    @JsonIgnore
    private UserInfo createdBy;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "equipements_id")
    private Equipements equipements;

    // 🔹 Relation inverse pour éviter boucle JSON
    @ManyToMany(mappedBy = "biensImmobiliers")
    @JsonBackReference
    @JsonIgnore
    private List<Contrats> contrats = new ArrayList<>();
    @CreatedDate
    @Column(updatable = false)
    private Date createdAt;

    @LastModifiedBy
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "updated_by_id") // Clé étrangère dans la table produits
    @JsonIgnore
    private UserInfo updatedBy;

    @LastModifiedDate
    private Date updatedAt;

    // ✅ Constructeur vide requis par JPA
    public BiensImmobiliers() {
    }

    // ✅ (facultatif) Votre constructeur personnalisé
    public BiensImmobiliers(String nom, Long id, String adresse, Double prix, UserInfo userInfo) {
        this.nom = nom;
        this.id = id;
        this.adresse = adresse;
        this.prix = prix;
        this.userInfo = userInfo;
    }

    public void incrementerVue() {
        this.nombreVues += 1;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    @Override
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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public TypeBiensImmobiliere getTypeBiensImmobiliere() {
        return typeBiensImmobiliere;
    }

    public void setTypeBiensImmobiliere(TypeBiensImmobiliere typeBiensImmobiliere) {
        this.typeBiensImmobiliere = typeBiensImmobiliere;
    }

    public double getSuperficie() {
        return superficie;
    }

    public void setSuperficie(double superficie) {
        this.superficie = superficie;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public TypeUnite getTypeUnite() {
        return typeUnite;
    }

    public void setTypeUnite(TypeUnite typeUnite) {
        this.typeUnite = typeUnite;
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserInfo getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserInfo createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public UserInfo getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UserInfo updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public Commune getCommune() {
        return commune;
    }

    public void setCommune(Commune commune) {
        this.commune = commune;
    }

    public Arrondissement getArrondissement() {
        return arrondissement;
    }

    public void setArrondissement(Arrondissement arrondissement) {
        this.arrondissement = arrondissement;
    }

    public void setSuperficie(Double superficie) {
        this.superficie = superficie;
    }

    public DetailsBiensImmobiliers getDetailsBiensImmobiliers() {
        return detailsBiensImmobiliers;
    }

    public void setDetailsBiensImmobiliers(DetailsBiensImmobiliers detailsBiensImmobiliers) {
        this.detailsBiensImmobiliers = detailsBiensImmobiliers;
    }

    public Double getNote() {
        return note;
    }

    public void setNote(Double note) {
        this.note = note;
    }

    public Integer getNombreVues() {
        return nombreVues;
    }

    public void setNombreVues(Integer nombreVues) {
        this.nombreVues = nombreVues;
    }

    public Equipements getEquipements() {
        return equipements;
    }

    public void setEquipements(Equipements equipements) {
        this.equipements = equipements;
    }

    public Integer getNombreJaime() {
        return nombreJaime;
    }

    public void setNombreJaime(Integer nombreJaime) {
        this.nombreJaime = nombreJaime;
    }

    public Boolean getaVendre() {
        return aVendre;
    }

    public void setaVendre(Boolean aVendre) {
        this.aVendre = aVendre;
    }

    public List<Contrats> getContrats() {
        return contrats;
    }

    public void setContrats(List<Contrats> contrats) {
        this.contrats = contrats;
    }
}
