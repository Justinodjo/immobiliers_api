package com.immoservice.immobiliers.entities;

import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

@Entity
public class Equipements {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(targetClass = Activite.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "equipements_activites", joinColumns = @JoinColumn(name = "equipements_id"))
    @Column(name = "activite")
    private Set<Activite> activites;

    @ElementCollection(targetClass = ModePaiement.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "equipements_paiements", joinColumns = @JoinColumn(name = "equipements_id"))
    @Column(name = "mode_paiement")
    private Set<ModePaiement> modesPaiement;

    @ElementCollection(targetClass = Service.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "equipements_services", joinColumns = @JoinColumn(name = "equipements_id"))
    @Column(name = "service")
    private Set<Service> services;

    @ElementCollection(targetClass = Securite.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "equipements_securite", joinColumns = @JoinColumn(name = "equipements_id"))
    @Column(name = "securite")
    private Set<Securite> securite;

    @ElementCollection(targetClass = LanguePersonnel.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "equipements_langues", joinColumns = @JoinColumn(name = "equipements_id"))
    @Column(name = "langue")
    private Set<LanguePersonnel> languesPersonnel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Activite> getActivites() {
        return activites;
    }

    public void setActivites(Set<Activite> activites) {
        this.activites = activites;
    }

    public Set<ModePaiement> getModesPaiement() {
        return modesPaiement;
    }

    public void setModesPaiement(Set<ModePaiement> modesPaiement) {
        this.modesPaiement = modesPaiement;
    }

    public Set<Service> getServices() {
        return services;
    }

    public void setServices(Set<Service> services) {
        this.services = services;
    }

    public Set<Securite> getSecurite() {
        return securite;
    }

    public void setSecurite(Set<Securite> securite) {
        this.securite = securite;
    }

    public Set<LanguePersonnel> getLanguesPersonnel() {
        return languesPersonnel;
    }

    public void setLanguesPersonnel(Set<LanguePersonnel> languesPersonnel) {
        this.languesPersonnel = languesPersonnel;
    }

    
}
