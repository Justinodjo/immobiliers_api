package com.immoservice.immobiliers.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.immoservice.immobiliers.entities.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
