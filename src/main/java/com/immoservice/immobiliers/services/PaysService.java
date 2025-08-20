package com.immoservice.immobiliers.services;

import com.immoservice.immobiliers.entities.Pays;
import com.immoservice.immobiliers.repository.PaysRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaysService {

    @Autowired
    private PaysRepository paysRepository;

    // Créer un nouveau pays
    public Pays ajouterPays(Pays pays) {
        pays.setDateCreation(LocalDate.now());
        pays.setVersion(LocalDate.now());
        return paysRepository.save(pays);
    }

    // Récupérer tous les pays
    public List<Pays> getAllPays() {
        return paysRepository.findAll();
    }

    // Récupérer un pays par son ID
    public Pays getPaysById(Long id) {
        return paysRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pays non trouvé avec l'ID : " + id));
    }

    // Mettre à jour un pays
    public Pays updatePays(Long id, Pays paysDetails) {
        Pays existingPays = getPaysById(id);
        existingPays.setLibelle(paysDetails.getLibelle());
        existingPays.setVersion(LocalDate.now());
        return paysRepository.save(existingPays);
    }

    // Supprimer un pays
    public void deletePays(Long id) {
        Pays pays = getPaysById(id);
        paysRepository.delete(pays);
    }
}
