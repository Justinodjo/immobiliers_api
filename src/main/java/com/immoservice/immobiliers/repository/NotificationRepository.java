package com.immoservice.immobiliers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
