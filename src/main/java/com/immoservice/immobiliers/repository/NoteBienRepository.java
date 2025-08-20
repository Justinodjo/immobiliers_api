package com.immoservice.immobiliers.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.BiensImmobiliers;
import com.immoservice.immobiliers.entities.NoteBien;
import com.immoservice.immobiliers.entities.UserInfo;

@Repository
public interface NoteBienRepository extends JpaRepository<NoteBien, Long> {
    List<NoteBien> findByBien(BiensImmobiliers bien);

    Optional<NoteBien> findByBienAndUtilisateur(BiensImmobiliers bien, UserInfo utilisateur);
}
