package com.immoservice.immobiliers.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.immoservice.immobiliers.entities.Activite;
import com.immoservice.immobiliers.entities.LanguePersonnel;
import com.immoservice.immobiliers.entities.ModePaiement;
import com.immoservice.immobiliers.entities.NiveauPriorite;
import com.immoservice.immobiliers.entities.Securite;
import com.immoservice.immobiliers.entities.Service;
import com.immoservice.immobiliers.entities.StatusContrat;
import com.immoservice.immobiliers.entities.StatutConsentement;
import com.immoservice.immobiliers.entities.StatutIntervention;
import com.immoservice.immobiliers.entities.StatutOffre;
import com.immoservice.immobiliers.entities.TypeAlertes;
import com.immoservice.immobiliers.entities.TypeConsentement;
import com.immoservice.immobiliers.entities.TypeContrat;
import com.immoservice.immobiliers.entities.TypeDetailsBiens;
import com.immoservice.immobiliers.entities.TypeDocument;
import com.immoservice.immobiliers.entities.TypeDonnee;
import com.immoservice.immobiliers.entities.TypeNotifications;
import com.immoservice.immobiliers.entities.TypeOffre;
import com.immoservice.immobiliers.entities.TypeSousDetailsBiens;
import com.immoservice.immobiliers.enums.EtatGeneral;
import com.immoservice.immobiliers.enums.TypeBalcon;
import com.immoservice.immobiliers.enums.TypeCaracteristique;
import com.immoservice.immobiliers.enums.TypeChauffage;
import com.immoservice.immobiliers.enums.TypeClimatisation;
import com.immoservice.immobiliers.enums.TypeCuisine;
import com.immoservice.immobiliers.enums.TypeDPE;
import com.immoservice.immobiliers.enums.TypeGarage;
import com.immoservice.immobiliers.enums.TypeIsolation;
import com.immoservice.immobiliers.enums.TypeMenuiserie;
import com.immoservice.immobiliers.enums.TypePiscine;
import com.immoservice.immobiliers.enums.TypePlafond;
import com.immoservice.immobiliers.enums.TypePorte;
import com.immoservice.immobiliers.enums.TypeRevetementMural;
import com.immoservice.immobiliers.enums.TypeSecurite;
import com.immoservice.immobiliers.enums.TypeSol;
import com.immoservice.immobiliers.enums.TypeTerrasse;
import com.immoservice.immobiliers.enums.TypeUnite;
import com.immoservice.immobiliers.enums.TypeVentilation;
import com.immoservice.immobiliers.enums.TypeVitrage;
import com.immoservice.immobiliers.enums.TypeVue;
import com.immoservice.immobiliers.enums.UniteSurface;
import com.immoservice.immobiliers.enums.UniteVolume;

@RestController
@RequestMapping("/enums")
public class TypesController {

    // Exemple pour TypeSousDetailsBiens
    @GetMapping("/type-sous-details")
    public TypeSousDetailsBiens[] getTypeSousDetailsBiens() {
        return TypeSousDetailsBiens.values();
    }

    // Exemple pour Service
    @GetMapping("/services")
    public Service[] getServices() {
        return Service.values();
    }

    // Exemple pour Securite
    @GetMapping("/securites")
    public Securite[] getSecurites() {
        return Securite.values();
    }

    @GetMapping("/activites")
    public Activite[] getActivites() {
        return Activite.values();
    }

    // Exemple pour TypeOffre
    @GetMapping("/type-offre")
    public TypeOffre[] getTypeOffres() {
        return TypeOffre.values();
    }

    // Exemple pour TypeNotifications
    @GetMapping("/type-notification")
    public TypeNotifications[] getTypeNotifications() {
        return TypeNotifications.values();
    }

    // Exemple pour TypeDonnee
    @GetMapping("/type-donnee")
    public TypeDonnee[] getTypeDonnees() {
        return TypeDonnee.values();
    }

    // Exemple pour TypeDocument
    @GetMapping("/type-document")
    public TypeDocument[] getTypeDocuments() {
        return TypeDocument.values();
    }

    // Exemple pour TypeDetailsBiens
    @GetMapping("/type-details-bien")
    public TypeDetailsBiens[] getTypeDetailsBiens() {
        return TypeDetailsBiens.values();
    }

    // Exemple pour TypeContrat
    @GetMapping("/type-contrat")
    public TypeContrat[] getTypeContrats() {
        return TypeContrat.values();
    }

    // Exemple pour TypeConsentement
    @GetMapping("/type-consentement")
    public TypeConsentement[] getTypeConsentements() {
        return TypeConsentement.values();
    }

    // Exemple pour TypeAlertes
    @GetMapping("/type-alertes")
    public TypeAlertes[] getTypeAlertes() {
        return TypeAlertes.values();
    }

    // Exemple pour StatutOffre
    @GetMapping("/statut-offres")
    public StatutOffre[] getStatutOffres() {
        return StatutOffre.values();
    }

    // Exemple pour StatutIntervention
    @GetMapping("/statut-interventions")
    public StatutIntervention[] getStatutInterventions() {
        return StatutIntervention.values();
    }

    // Exemple pour StatutConsentement
    @GetMapping("/statut-consentements")
    public StatutConsentement[] getStatutConsentements() {
        return StatutConsentement.values();
    }

    // Exemple pour StatusContrat
    @GetMapping("/status-contrats")
    public StatusContrat[] getStatusContrats() {
        return StatusContrat.values();
    }

    // Exemple pour NiveauPriorite
    @GetMapping("/niveaux-priorite")
    public NiveauPriorite[] getNiveauxPriorite() {
        return NiveauPriorite.values();
    }

    // Exemple pour ModePaiement
    @GetMapping("/mode-paiement")
    public ModePaiement[] getModePaiement() {
        return ModePaiement.values();
    }

    // Exemple pour LanguePersonnel
    @GetMapping("/langue-personnel")
    public LanguePersonnel[] getLanguePersonnel() {
        return LanguePersonnel.values();
    }

    // Exemple pour TypeVue
    @GetMapping("/type-vue")
    public TypeVue[] getTypeVue() {
        return TypeVue.values();
    }

    // Exemple pour TypeVitrage
    @GetMapping("/type-vitrage")
    public TypeVitrage[] getTypeVitrages() {
        return TypeVitrage.values();
    }

    // Exemple pour TypeVentilation
    @GetMapping("/type-ventilation")
    public TypeVentilation[] getTypeVentilations() {
        return TypeVentilation.values();
    }

    // Exemple pour TypeUnite
    @GetMapping("/type-unite")
    public TypeUnite[] getTypeUnites() {
        return TypeUnite.values();
    }

    // Exemple pour TypeTerrasse
    @GetMapping("/type-terrasse")
    public TypeTerrasse[] getTypeTerrasses() {
        return TypeTerrasse.values();
    }

    // Exemple pour TypeTerrasse
    @GetMapping("/type-securite")
    public TypeSecurite[] getTypeSecurites() {
        return TypeSecurite.values();
    }

    // Exemple pour TypeRevetementMural
    @GetMapping("/type-revetement-mural")
    public TypeRevetementMural[] getTypeRevetementMurals() {
        return TypeRevetementMural.values();
    }

    // Exemple pour TypePorte
    @GetMapping("/type-porte")
    public TypePorte[] getTypePortes() {
        return TypePorte.values();
    }

    // Exemple pour TypeSol
    @GetMapping("/type-sol")
    public TypeSol[] getTypeSols() {
        return TypeSol.values();
    }

    // Exemple pour TypeMenuiserie
    @GetMapping("/type-menuiserie")
    public TypeMenuiserie[] getTypeMenuiseries() {
        return TypeMenuiserie.values();
    }

    // Exemple pour TypePlafond
    @GetMapping("/type-plafond")
    public TypePlafond[] getTypePlafonds() {
        return TypePlafond.values();
    }

    // Exemple pour TypePiscine
    @GetMapping("/type-piscine")
    public TypePiscine[] getTypePiscines() {
        return TypePiscine.values();
    }

    // Exemple pour TypeIsolation
    @GetMapping("/type-isolation")
    public TypeIsolation[] getTypeIsolations() {
        return TypeIsolation.values();
    }

    // Exemple pour TypeIsolation
    @GetMapping("/type-garage")
    public TypeGarage[] getTypeGarages() {
        return TypeGarage.values();
    }

    // Exemple pour TypeCuisine
    @GetMapping("/type-cuisine")
    public TypeCuisine[] getTypeCuisines() {
        return TypeCuisine.values();
    }

    // Exemple pour TypeClimatisation
    @GetMapping("/type-climatisation")
    public TypeClimatisation[] getTypeClimatisations() {
        return TypeClimatisation.values();
    }

    // Exemple pour TypeChauffage
    @GetMapping("/type-chauffage")
    public TypeChauffage[] getTypeChauffages() {
        return TypeChauffage.values();
    }

    // Exemple pour TypeCaracteristique
    @GetMapping("/type-caracteristique")
    public TypeCaracteristique[] getTypeCaracteristiques() {
        return TypeCaracteristique.values();
    }

    // Exemple pour TypeBalcon
    @GetMapping("/type-balcon")
    public TypeBalcon[] getTypeBalcons() {
        return TypeBalcon.values();
    }

    // Exemple pour TypeBalcon
    @GetMapping("/etat-general")
    public EtatGeneral[] getEtatGenerals() {
        return EtatGeneral.values();
    }

    // Exemple pour TypeDPE
    @GetMapping("/type-dpe")
    public TypeDPE[] getTypeDPE() {
        return TypeDPE.values();
    }

    // Exemple pour UniteSurface
    @GetMapping("/unite-surface")
    public UniteSurface[] getUniteSurfaces() {
        return UniteSurface.values();
    }

    // Exemple pour UniteVolume
    @GetMapping("/unite-volume")
    public UniteVolume[] getUniteVolumes() {
        return UniteVolume.values();
    }
}
