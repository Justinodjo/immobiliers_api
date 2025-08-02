package com.immoservice.immobiliers.services;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.entities.TypeBiensImmobiliere;
import com.immoservice.immobiliers.repository.TypeBiensImmobiliereRepository;

@Service
public class TypeBiensImmobiliereService {
    @Autowired
    private TypeBiensImmobiliereRepository typeBiensImmobiliereRepository;

    public TypeBiensImmobiliere saveTypeBiensImmobiliere(TypeBiensImmobiliere typeBiensImmobiliere) {
        return typeBiensImmobiliereRepository.save(typeBiensImmobiliere);
    }

    public List<TypeBiensImmobiliere> getAllTypeBiensImmobilieres() {
        return typeBiensImmobiliereRepository.findAll();
    }

    public Optional<TypeBiensImmobiliere> getTypeBiensImmobiliereById(Long id) {
        return typeBiensImmobiliereRepository.findById(id);
    }

    public void deleteTypeBiensImmobiliere(Long id) {
        typeBiensImmobiliereRepository.deleteById(id);
    }

    // public List<TypeBiensImmobiliere> getTypeBiensImmobilieresByCategorieId(Long
    // biens_immobiliersId) {
    // return
    // typeBiensImmobiliereRepository.findByBiens_immobiliersId(biens_immobiliersId);
    // }

    public TypeBiensImmobiliere updateTypeBiensImmobiliere(Long id, TypeBiensImmobiliere updated) {
        return typeBiensImmobiliereRepository.findById(id)
                .map(existing -> {
                    existing.setNomTypeBiensImmo(updated.getNomTypeBiensImmo());
                    existing.setDescriptif(updated.getDescriptif());
                    // Ajoute les autres champs nécessaires
                    return typeBiensImmobiliereRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("TypeService non trouvé avec l'id : " + id));
    }

    // public TypeService updateTypeService(Long id, TypeService typeService) {
    // if (typeServiceRepository.existsById(id)) {
    // typeService.setIdTypeService(id);
    // return roleRepository.save(typeService);
    // }
    // return null;
    // }
}