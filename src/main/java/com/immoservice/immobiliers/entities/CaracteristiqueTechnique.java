package com.immoservice.immobiliers.entities;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.immoservice.immobiliers.enums.TypeCaracteristique;

@Entity
public class CaracteristiqueTechnique implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean possede = false;

    @Enumerated(EnumType.STRING)
    private TypeCaracteristique typeCaracteristique;

    @ManyToOne
    @JoinColumn(name = "sous_details_id")
    @JsonManagedReference("caracteristiqueTechnique-sousDetailsBiensImmobiliers")
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
}
