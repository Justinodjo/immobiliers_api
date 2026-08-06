package com.immoservice.immobiliers.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.immoservice.immobiliers.enums.EtatGeneral;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
 @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class SousDetailsBiensImmobiliers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TypeSousDetailsBiens typeSousDetailsBiens;
    @Enumerated(EnumType.STRING)
    private EtatGeneral etatGeneral;
    private Integer nombre;
    private Integer nombreEtage;
    private Integer anneeConstruction;
    private String description;
    private Double superficie;
    @ManyToOne
    @JoinColumn(name = "details_biens_immobiliers_id")
    // @JsonBackReference("detailsBiensImmobiliers-sousDetailsBiensImmobiliers")
    private DetailsBiensImmobiliers detailsBiensImmobiliers;

    @OneToMany(mappedBy = "sousDetailsBiensImmobiliers", cascade = CascadeType.ALL, orphanRemoval = true)
    // @JsonManagedReference("caracteristiqueTechnique-sousDetailsBiensImmobiliers")
    private List<CaracteristiqueTechnique> caracteristiques = new ArrayList<>();

    public List<CaracteristiqueTechnique> getCaracteristiques() {
        if (caracteristiques == null) {
            caracteristiques = new ArrayList<>();
        }
        return caracteristiques;
    }

    public void setCaracteristiques(List<CaracteristiqueTechnique> caracteristiques) {
        this.caracteristiques = caracteristiques;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeSousDetailsBiens getTypeSousDetailsBiens() {
        return typeSousDetailsBiens;
    }

    public void setTypeSousDetailsBiens(TypeSousDetailsBiens typeSousDetailsBiens) {
        this.typeSousDetailsBiens = typeSousDetailsBiens;
    }

    public Integer getNombre() {
        return nombre;
    }

    public void setNombre(Integer nombre) {
        this.nombre = nombre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getSuperficie() {
        return superficie;
    }

    public void setSuperficie(Double superficie) {
        this.superficie = superficie;
    }

    public void addCaracteristique(CaracteristiqueTechnique caracteristique) {
        caracteristiques.add(caracteristique);
        caracteristique.setSousDetailsBiensImmobiliers(this);
    }

    public void removeCaracteristique(CaracteristiqueTechnique caracteristique) {
        caracteristiques.remove(caracteristique);
        caracteristique.setSousDetailsBiensImmobiliers(null);
    }

    @Override
    public String toString() {
        return "SousDetailsBiensImmobiliers{" +
                "id=" + id +
                ", type=" + typeSousDetailsBiens +
                ", nombre=" + nombre +
                ", description='" + description + '\'' +
                ", superficie=" + superficie +
                '}';
    }

    public DetailsBiensImmobiliers getDetailsBiensImmobiliers() {
        return detailsBiensImmobiliers;
    }

    // Méthode pour gérer l'association bidirectionnelle
    public void setDetailsBiensImmobiliers(DetailsBiensImmobiliers detailsBiensImmobiliers) {
        this.detailsBiensImmobiliers = detailsBiensImmobiliers;
        if (detailsBiensImmobiliers != null
                && !detailsBiensImmobiliers.getSousDetailsBiensImmobiliers().contains(this)) {
            detailsBiensImmobiliers.getSousDetailsBiensImmobiliers().add(this); // Associe cet objet dans la liste
        }
    }

    public void removeDetailsBiensImmobilier() {
        if (this.detailsBiensImmobiliers != null) {
            this.detailsBiensImmobiliers.setSousDetailsBiensImmobiliers(null); // Dissocie l'objet de l'autre côté
            this.detailsBiensImmobiliers = null; // Dissocie la relation de ce côté
        }
    }

    public Integer getNombreEtage() {
        return nombreEtage;
    }

    public void setNombreEtage(Integer nombreEtage) {
        this.nombreEtage = nombreEtage;
    }

    public Integer getAnneeConstruction() {
        return anneeConstruction;
    }

    public void setAnneeConstruction(Integer anneeConstruction) {
        this.anneeConstruction = anneeConstruction;
    }

    public EtatGeneral getEtatGeneral() {
        return etatGeneral;
    }

    public void setEtatGeneral(EtatGeneral etatGeneral) {
        this.etatGeneral = etatGeneral;
    }
}
