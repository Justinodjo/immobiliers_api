package com.immoservice.immobiliers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByExpediteurId(Integer expediteurId);

    List<Message> findByDestinataireId(Integer destinataireId);
}
