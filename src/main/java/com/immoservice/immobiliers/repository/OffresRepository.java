package com.immoservice.immobiliers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.immoservice.immobiliers.entities.Offres;

@Repository
public interface OffresRepository extends JpaRepository<Offres, Long> {

}
