package com.immoservice.immobiliers.services;

import com.immoservice.immobiliers.entities.SousDetailsBiensImmobiliers;
import com.immoservice.immobiliers.repository.SousDetailsBiensImmobiliersRepository;
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

    public Optional<SousDetailsBiensImmobiliers> getById(Long id) {
        return sousDetailsBiensImmobiliersRepository.findById(id);
    }

    public SousDetailsBiensImmobiliers save(SousDetailsBiensImmobiliers sousDetailsBiensImmobiliers) {
        return sousDetailsBiensImmobiliersRepository.save(sousDetailsBiensImmobiliers);
    }

    public SousDetailsBiensImmobiliers update(Long id, SousDetailsBiensImmobiliers updatedSousDetailsBiensImmobiliers) {
        return sousDetailsBiensImmobiliersRepository.findById(id).map(existing -> {
            existing.setTypeSousDetailsBiens(updatedSousDetailsBiensImmobiliers.getTypeSousDetailsBiens());
            existing.setNombre(updatedSousDetailsBiensImmobiliers.getNombre());
            existing.setDescription(updatedSousDetailsBiensImmobiliers.getDescription());
            return sousDetailsBiensImmobiliersRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SousDetailsBiensImmobiliers non trouvé avec id : " + id));
    }

    public void delete(Long id) {
        sousDetailsBiensImmobiliersRepository.deleteById(id);
    }
}
