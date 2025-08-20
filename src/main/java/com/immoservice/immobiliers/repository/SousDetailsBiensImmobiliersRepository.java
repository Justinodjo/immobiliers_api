package com.immoservice.immobiliers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.SousDetailsBiensImmobiliers;

@Repository
public interface SousDetailsBiensImmobiliersRepository extends JpaRepository<SousDetailsBiensImmobiliers, Long> {

    List<SousDetailsBiensImmobiliers> findByTypeSousDetailsBiens(String type);

    List<SousDetailsBiensImmobiliers> findByTypeSousDetailsBiensAndNombre(String type, Integer nombre);

}
