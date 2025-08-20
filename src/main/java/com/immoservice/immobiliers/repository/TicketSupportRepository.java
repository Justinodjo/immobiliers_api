package com.immoservice.immobiliers.repository;

import com.immoservice.immobiliers.entities.TicketSupport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketSupportRepository extends JpaRepository<TicketSupport, Long> {
    List<TicketSupport> findByCreateurId(Integer userId);

    List<TicketSupport> findByStatut(String statut);
}
