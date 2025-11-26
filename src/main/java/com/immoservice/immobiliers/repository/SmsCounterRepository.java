package com.immoservice.immobiliers.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.SmsCounter;

@Repository
public interface SmsCounterRepository extends JpaRepository<SmsCounter, Long> {
    Optional<SmsCounter> findByPhoneNumberAndDay(String phoneNumber, LocalDate day);
}
