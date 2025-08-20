package com.immoservice.immobiliers.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.Contrats;
import com.immoservice.immobiliers.entities.StatusContrat;
import com.immoservice.immobiliers.entities.TypeContrat;
import com.immoservice.immobiliers.entities.UserInfo;

@Repository
public interface ContratsRepository extends JpaRepository<Contrats, Long> {

    int countByBiensImmobiliers_Id(Long id);

    List<Contrats> findByUserInfoEmail(String email);

    List<Contrats> findByUserInfo(UserInfo user);

    List<Contrats> findByTypeContrat(TypeContrat typeContrat);

    List<Contrats> findByStatus(String status);

    List<Contrats> findByGestionnaire(UserInfo gestionnaire);

    List<Contrats> findByDateFinBeforeAndStatusNot(LocalDate dateFin, StatusContrat status);

}