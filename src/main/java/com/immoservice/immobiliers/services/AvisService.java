package com.immoservice.immobiliers.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.entities.Avis;
import com.immoservice.immobiliers.repository.AvisRepository;

@Service
public class AvisService {
    @Autowired
    private AvisRepository avisRepository;

    public Avis creerAvis(Avis avis) {
        return avisRepository.save(avis);
    }

    public List<Avis> recupererTous() {
        return avisRepository.findAll();
    }

    public Optional<Avis> recupererParId(Long id) {
        return avisRepository.findById(id);
    }

    public Avis mettreAJour(Long id, Avis avisDetails) {
        return avisRepository.findById(id)
                .map(avis -> {
                    avis.setNom(avisDetails.getNom());
                    avis.setDateSejour(avisDetails.getDateSejour());
                    avis.setNombreAvis(avisDetails.getNombreAvis());
                    avis.setNote(avisDetails.getNote());
                    avis.setCommentaire(avisDetails.getCommentaire());
                    avis.setRole(avisDetails.getRole());
                    avis.setFichier(avisDetails.getFichier());
                    avis.setFichiers(avisDetails.getFichiers());
                    avis.setUserInfo(avisDetails.getUserInfo());
                    avis.setBiensImmobiliers(avisDetails.getBiensImmobiliers());
                    return avisRepository.save(avis);
                })
                .orElseThrow(() -> new RuntimeException("Avis introuvable avec id " + id));
    }

    public void supprimer(Long id) {
        avisRepository.deleteById(id);
    }

    public List<Avis> recupererParBienId(Long id) {
        return avisRepository.findByBiensImmobiliersId(id);
    }
}
