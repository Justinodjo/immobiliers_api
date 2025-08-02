package com.immoservice.immobiliers.repository;
import com.immoservice.immobiliers.entities.Paiements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PaiementsRepository extends JpaRepository<Paiements, Long>{
    
}