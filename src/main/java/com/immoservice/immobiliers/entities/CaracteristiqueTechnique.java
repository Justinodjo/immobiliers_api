package com.immoservice.immobiliers.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonBackReference;
// import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.immoservice.immobiliers.enums.TypeCaracteristique;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
 @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class CaracteristiqueTechnique implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean possede = false;
    private String libelle;
    @Enumerated(EnumType.STRING)
    private TypeCaracteristique typeCaracteristique;

    @ManyToOne
    @JoinColumn(name = "sous_details_id")
    // @JsonManagedReference("caracteristiqueTechnique-sousDetailsBiensImmobiliers")
    // @JsonBackReference("caracteristiqueTechnique-sousDetailsBiensImmobiliers")
    private SousDetailsBiensImmobiliers sousDetailsBiensImmobiliers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CaracteristiqueTechnique that = (CaracteristiqueTechnique) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CaracteristiqueTechnique{" +
                "id=" + id +
                ", possede=" + possede +
                ", typeCaracteristique=" + typeCaracteristique +
                '}';
    }

    public Boolean getPossede() {
        return possede;
    }

    public void setPossede(Boolean possede) {
        this.possede = possede;
    }

    public TypeCaracteristique getTypeCaracteristique() {
        return typeCaracteristique;
    }

    public void setTypeCaracteristique(TypeCaracteristique typeCaracteristique) {
        this.typeCaracteristique = typeCaracteristique;
    }

    public SousDetailsBiensImmobiliers getSousDetailsBiensImmobiliers() {
        return sousDetailsBiensImmobiliers;
    }

    public void setSousDetailsBiensImmobiliers(SousDetailsBiensImmobiliers sousDetailsBiensImmobiliers) {
        this.sousDetailsBiensImmobiliers = sousDetailsBiensImmobiliers;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}
