package com.immoservice.immobiliers.services;

import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.immoservice.immobiliers.entities.BiensImmobiliers;
import com.immoservice.immobiliers.entities.Fichier;
import com.immoservice.immobiliers.entities.TypeBiensImmobiliere;
import com.immoservice.immobiliers.repository.BiensImmobiliersRepository;
import com.immoservice.immobiliers.repository.FichierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@Service
public class BiensImmobiliersService {
    @Autowired
    private BiensImmobiliersRepository biensImmobiliersRepository;
    @Autowired
    private FichierRepository fichierRepository;

    @Transactional
    public BiensImmobiliers save(BiensImmobiliers biensImmobiliers) {
        // Vérifier si le fichier principal existe en base
        if (biensImmobiliers.getFichier() != null && biensImmobiliers.getFichier().getId() != null) {
            biensImmobiliers.setFichier(fichierRepository.findById(biensImmobiliers.getFichier().getId())
                    .orElseThrow(() -> new RuntimeException("Fichier non trouvé")));
        }

        // Vérifier si les fichiers secondaires existent en base
        if (biensImmobiliers.getFichiers() != null && !biensImmobiliers.getFichiers().isEmpty()) {
            List<Fichier> fichiersAttached = biensImmobiliers.getFichiers().stream()
                    .map(fichier -> fichierRepository.findById(fichier.getId())
                            .orElseThrow(() -> new RuntimeException("Fichier non trouvé : " + fichier.getId())))
                    .collect(Collectors.toList());

            biensImmobiliers.setFichiers(fichiersAttached);
        }
        return biensImmobiliersRepository.save(biensImmobiliers);
    }

    public Page<BiensImmobiliers> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return biensImmobiliersRepository.findAll(pageable);
    }

    public Optional<BiensImmobiliers> getById(Long id) {
        return biensImmobiliersRepository.findById(id);
    }

    public void delete(Long id) {
        biensImmobiliersRepository.deleteById(id);
    }

public List<BiensImmobiliers> searchBiensByTextAndType(String texte, Long typeId) {
    if ((texte == null || texte.isEmpty()) && typeId == null) {
        return biensImmobiliersRepository.findAll();
    }
    return biensImmobiliersRepository.searchByTextAndType(
        texte != null ? texte : "", typeId
    );
}

public BiensImmobiliers updateBiensImmobiliers(Long id, BiensImmobiliers biensImmobiliers) {
    if (biensImmobiliersRepository.existsById(id)) {
        biensImmobiliers.setId(id);
        return biensImmobiliersRepository.save(biensImmobiliers);
    }
    return null;
}

    //Récupérer les biens immobiliers par type avec pagination
    public Page<BiensImmobiliers> getBiensByType(TypeBiensImmobiliere typeBiensImmobiliere, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return biensImmobiliersRepository.findByTypeBiensImmobiliere(typeBiensImmobiliere, pageable);
    }
    

    public BiensImmobiliers consulterBien(Long id) {
        BiensImmobiliers bien = biensImmobiliersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bien non trouvé"));

        bien.incrementerVue();
        return biensImmobiliersRepository.save(bien);
    }

    public Optional<BiensImmobiliers> incrementerJaime(Long id) {
        return biensImmobiliersRepository.findById(id).map(bien -> {
            bien.setNombreJaime(bien.getNombreJaime() + 1);
            return biensImmobiliersRepository.save(bien);
        });
    }
}