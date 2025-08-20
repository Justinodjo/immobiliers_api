package com.immoservice.immobiliers.services;

import com.immoservice.immobiliers.entities.SousDetailsBiensImmobiliers;
import com.immoservice.immobiliers.repository.SousDetailsBiensImmobiliersRepository;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SousDetailsBiensImmobiliersService {

    @Autowired
    private SousDetailsBiensImmobiliersRepository sousDetailsBiensImmobiliersRepository;

    public List<SousDetailsBiensImmobiliers> getAll() {
        return sousDetailsBiensImmobiliersRepository.findAll();
    }

    public List<SousDetailsBiensImmobiliers> getByType(String type) {
        return sousDetailsBiensImmobiliersRepository.findByTypeSousDetailsBiens(type);
    }

    public List<SousDetailsBiensImmobiliers> getByTypeAndNombre(String type, Integer nombre) {
        return sousDetailsBiensImmobiliersRepository.findByTypeSousDetailsBiensAndNombre(type, nombre);
    }

    public Optional<SousDetailsBiensImmobiliers> getById(Long id) {
        return sousDetailsBiensImmobiliersRepository.findById(id);
    }

    @Transactional
    public SousDetailsBiensImmobiliers save(SousDetailsBiensImmobiliers sousDetailsBiensImmobiliers) {
        // Si l'entité est détachée, utiliser merge (ou save normalement si c'est une
        // nouvelle entité)
        if (sousDetailsBiensImmobiliers.getId() != null) {
            return sousDetailsBiensImmobiliersRepository.save(sousDetailsBiensImmobiliers);
        } else {
            return sousDetailsBiensImmobiliersRepository.save(sousDetailsBiensImmobiliers);
        }
    }

    @Transactional
    public SousDetailsBiensImmobiliers update(Long id, SousDetailsBiensImmobiliers updatedSousDetailsBiensImmobiliers) {
        return sousDetailsBiensImmobiliersRepository.findById(id).map(existing -> {
            existing.setTypeSousDetailsBiens(updatedSousDetailsBiensImmobiliers.getTypeSousDetailsBiens());
            existing.setNombre(updatedSousDetailsBiensImmobiliers.getNombre());
            existing.setDescription(updatedSousDetailsBiensImmobiliers.getDescription());
            return sousDetailsBiensImmobiliersRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SousDetailsBiensImmobiliers non trouvé avec id : " + id));
    }

    @Transactional
    public void delete(Long id) {
        sousDetailsBiensImmobiliersRepository.deleteById(id);
    }
}
