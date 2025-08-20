package com.immoservice.immobiliers.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.entities.BiensImmobiliers;
import com.immoservice.immobiliers.entities.NoteBien;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.BiensImmobiliersRepository;
import com.immoservice.immobiliers.repository.NoteBienRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteBienService {

    @Autowired
    private NoteBienRepository noteBienRepository;
    @Autowired
    private BiensImmobiliersRepository biensRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;

    public void noterBien(Long bienId, Integer utilisateurId, int note) {
        BiensImmobiliers bien = biensRepository.findById(bienId)
                .orElseThrow(() -> new RuntimeException("Bien introuvable"));

        UserInfo utilisateur = userInfoRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Vérifie si l'utilisateur a déjà noté ce bien
        Optional<NoteBien> existing = noteBienRepository.findByBienAndUtilisateur(bien, utilisateur);
        if (existing.isPresent()) {
            existing.get().setNote(note);
            noteBienRepository.save(existing.get());
        } else {
            NoteBien newNote = new NoteBien();
            newNote.setNote(note);
            newNote.setBien(bien);
            newNote.setUtilisateur(utilisateur);
            noteBienRepository.save(newNote);
        }

        // Recalculer la note moyenne
        List<NoteBien> notes = noteBienRepository.findByBien(bien);
        double moyenne = notes.stream()
                .mapToInt(NoteBien::getNote)
                .average()
                .orElse(0);

        bien.setNote(moyenne);
        biensRepository.save(bien);
    }
}
