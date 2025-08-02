package com.immoservice.immobiliers.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.ConsentementRGPD;
import com.immoservice.immobiliers.entities.UserInfo;

@Repository
public interface ConsentementRGPDRepository extends JpaRepository<ConsentementRGPD, Long> {
    List<ConsentementRGPD> findByUser(UserInfo user);
}
