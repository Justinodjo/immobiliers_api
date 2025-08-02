package com.immoservice.immobiliers.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TypeBiensImmobiliere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomTypeBiensImmo;
    private String descriptif;

    public TypeBiensImmobiliere() {
    }

    public TypeBiensImmobiliere(Long id, String nomTypeBiensImmo) {
        this.id = id;
        this.nomTypeBiensImmo = nomTypeBiensImmo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomTypeBiensImmo() {
        return nomTypeBiensImmo;
    }

    public void setNomTypeBiensImmo(String nomTypeBiensImmo) {
        this.nomTypeBiensImmo = nomTypeBiensImmo;
    }

    public String getDescriptif() {
        return descriptif;
    }

    public void setDescriptif(String descriptif) {
        this.descriptif = descriptif;
    }

}
