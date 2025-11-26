package com.immoservice.immobiliers.entities;
import java.time.LocalDate;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ContratDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Contrats contrat;

    @ManyToOne
    private DetailsBiensImmobiliers detail;

    private Double prixSpecifique;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    
}
