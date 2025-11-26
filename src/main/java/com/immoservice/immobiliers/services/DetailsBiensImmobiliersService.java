package com.immoservice.immobiliers.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.immoservice.immobiliers.entities.BiensImmobiliers;
import com.immoservice.immobiliers.entities.DetailsBiensImmobiliers;
import com.immoservice.immobiliers.entities.Fichier;
import com.immoservice.immobiliers.repository.BiensImmobiliersRepository;
import com.immoservice.immobiliers.repository.DetailsBiensImmobiliersRepository;
import com.immoservice.immobiliers.repository.FichierRepository;

@Service
public class DetailsBiensImmobiliersService {
    @Autowired
    private DetailsBiensImmobiliersRepository detailsBiensImmobiliersRepository;

    @Autowired
    private BiensImmobiliersRepository biensImmobiliersRepository;

    @Autowired
    private FichierRepository fichierRepository;

    @Transactional
    public DetailsBiensImmobiliers save(DetailsBiensImmobiliers detailsBiensImmobiliers, Long bienId) {
        // Vérifier si le fichier principal existe en base
        if (detailsBiensImmobiliers.getFichier() != null && detailsBiensImmobiliers.getFichier().getId() != null) {
            detailsBiensImmobiliers.setFichier(fichierRepository.findById(detailsBiensImmobiliers.getFichier().getId())
                    .orElseThrow(() -> new RuntimeException("Fichier non trouvé")));
        }

        // Vérifier si les fichiers secondaires existent en base
        if (detailsBiensImmobiliers.getFichiers() != null && !detailsBiensImmobiliers.getFichiers().isEmpty()) {
            List<Fichier> fichiersAttached = detailsBiensImmobiliers.getFichiers().stream()
                    .map(fichier -> fichierRepository.findById(fichier.getId())
                            .orElseThrow(() -> new RuntimeException("Fichier non trouvé : " + fichier.getId())))
                    .collect(Collectors.toList());

            detailsBiensImmobiliers.setFichiers(fichiersAttached);
        }

        // Lier le détail au bien
        BiensImmobiliers bien = biensImmobiliersRepository.findById(bienId)
                .orElseThrow(() -> new RuntimeException("Bien non trouvé"));
        detailsBiensImmobiliers.setBiensImmobiliers(bien);
        bien.getDetailsBiensImmobiliers().add(detailsBiensImmobiliers);
        return detailsBiensImmobiliersRepository.save(detailsBiensImmobiliers);
    }

    public List<DetailsBiensImmobiliers> getAll() {
        return detailsBiensImmobiliersRepository.findAll();
    }

    public Optional<DetailsBiensImmobiliers> getById(Long id) {
        return detailsBiensImmobiliersRepository.findById(id);
    }

    public void delete(Long id) {
        detailsBiensImmobiliersRepository.deleteById(id);
    }

    public DetailsBiensImmobiliers updateDetailsBiensImmobiliers(Long id,
            DetailsBiensImmobiliers detailsBiensImmobiliers) {
        if (detailsBiensImmobiliersRepository.existsById(id)) {
            detailsBiensImmobiliers.setId(id);
            return detailsBiensImmobiliersRepository.save(detailsBiensImmobiliers);
        }
        return null;
    }

}
