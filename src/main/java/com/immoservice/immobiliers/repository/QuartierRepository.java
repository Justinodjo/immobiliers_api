package com.immoservice.immobiliers.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.immoservice.immobiliers.entities.Quartier;
import com.immoservice.immobiliers.entities.Arrondissement;
@Repository
public interface QuartierRepository extends JpaRepository<Quartier, Long> {
List<Quartier> findByArrondissement(Arrondissement arrondissement);
}