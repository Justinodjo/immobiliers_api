package com.immoservice.immobiliers.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.immoservice.immobiliers.entities.PasswordResetToken;
import com.immoservice.immobiliers.entities.UserInfo;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.userInfo = :user")
    void deleteByUserInfo(@Param("user") UserInfo user);

    List<PasswordResetToken> findByExpiryDateBefore(Date date);

    Optional<PasswordResetToken> findByUserInfoAndUsedFalse(UserInfo userInfo);
}