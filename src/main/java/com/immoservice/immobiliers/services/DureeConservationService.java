package com.immoservice.immobiliers.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.entities.BaseLegale;
import com.immoservice.immobiliers.entities.DureeConservation;
import com.immoservice.immobiliers.entities.TypeDonnee;
import com.immoservice.immobiliers.repository.DureeConservationRepository;

@Service
public class DureeConservationService {

    private final DureeConservationRepository repository;

    public DureeConservationService(DureeConservationRepository repository) {
        this.repository = repository;
    }

    public DureeConservation save(DureeConservation duree) {
        return repository.save(duree);
    }

    public Page<DureeConservation> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<DureeConservation> findById(Long id) {
        return repository.findById(id);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<DureeConservation> findByTypeDonnee(TypeDonnee typeDonnee, Pageable pageable) {
        return repository.findByTypeDonnee(typeDonnee, pageable);
    }

    public Page<DureeConservation> findByBaseLegale(BaseLegale baseLegale, Pageable pageable) {
        return repository.findByBaseLegale(baseLegale, pageable);
    }
}
