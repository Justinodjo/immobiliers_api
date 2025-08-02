package com.immoservice.immobiliers.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SousDetailsBiensImmobiliers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TypeSousDetailsBiens typeSousDetailsBiens;
    private Integer nombre;
    private String description;

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

}
