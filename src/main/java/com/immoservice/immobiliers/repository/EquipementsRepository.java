package com.immoservice.immobiliers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.Equipements;

@Repository
public interface EquipementsRepository extends JpaRepository<Equipements, Long> {

}
