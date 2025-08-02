package com.immoservice.immobiliers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.Departement;

@Repository
public interface DepartementRepository extends JpaRepository<Departement, Long> {

}
