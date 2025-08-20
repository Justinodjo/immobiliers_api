package com.immoservice.immobiliers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.Departement;
import com.immoservice.immobiliers.entities.Pays;

@Repository
public interface DepartementRepository extends JpaRepository<Departement, Long> {
    List<Departement> findByPays(Pays pays);
}
