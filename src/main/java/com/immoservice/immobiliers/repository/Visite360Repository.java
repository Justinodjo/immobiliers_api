package com.immoservice.immobiliers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.immoservice.immobiliers.entities.Visite360;

@Repository
public interface Visite360Repository extends JpaRepository<Visite360, Long> {

    List<Visite360> findByBienId(Long bienId);
}
