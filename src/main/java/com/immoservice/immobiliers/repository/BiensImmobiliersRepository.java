package com.immoservice.immobiliers.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.BiensImmobiliers;

@Repository
public interface BiensImmobiliersRepository extends JpaRepository<BiensImmobiliers, Long> {
     List<BiensImmobiliers> findByUserInfoEmail(String email);
     List<BiensImmobiliers> findByTypeBiensImmobiliereId(Long typeId);
     List<BiensImmobiliers> findByUserInfoId(Integer userId);
}