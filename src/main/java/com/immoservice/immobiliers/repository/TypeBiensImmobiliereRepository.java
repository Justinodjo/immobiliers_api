package com.immoservice.immobiliers.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.TypeBiensImmobiliere;

@Repository
public interface TypeBiensImmobiliereRepository extends JpaRepository<TypeBiensImmobiliere, Long> {

    Optional<TypeBiensImmobiliere> findByNomTypeBiensImmo(String nomTypeBiensImmo);

}