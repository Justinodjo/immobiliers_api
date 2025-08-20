package com.immoservice.immobiliers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.CaracteristiqueTechnique;

@Repository
public interface CaracteristiqueTechniqueRepository extends JpaRepository<CaracteristiqueTechnique, Long> {
    List<CaracteristiqueTechnique> findBySousDetailsBiensImmobiliersId(Long sousDetailsId);
}
