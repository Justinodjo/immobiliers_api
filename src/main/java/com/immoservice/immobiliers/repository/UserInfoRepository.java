package com.immoservice.immobiliers.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.UserInfo;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    Optional<UserInfo> findByName(String userName);

    Optional<UserInfo> findByEmail(String email);

    Optional<UserInfo> findByNameOrEmail(String userName, String email);

    Optional<UserInfo> findById(Long id);
}
