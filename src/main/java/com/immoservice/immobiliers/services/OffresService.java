// OffresService.java
package com.immoservice.immobiliers.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.entities.Offres;
import com.immoservice.immobiliers.repository.OffresRepository;

@Service
public class OffresService {

    @Autowired
    private OffresRepository offresRepository;

    public List<Offres> getAllOffres() {
        return offresRepository.findAll();
    }

    public Page<Offres> getOffresActives(Pageable pageable) {
        Date now = new Date();
        return offresRepository.findByDateDebutLessThanEqualAndDateFinGreaterThanEqual(now, now, pageable);
    }

    public Offres getOffreById(Long id) {
        return offresRepository.findById(id).orElse(null);
    }

    public Offres createOffre(Offres offre) {
        return offresRepository.save(offre);
    }

    public Offres updateOffre(Long id, Offres offre) {
        offre.setId(id);
        return offresRepository.save(offre);
    }

    public void deleteOffre(Long id) {
        offresRepository.deleteById(id);
    }
}