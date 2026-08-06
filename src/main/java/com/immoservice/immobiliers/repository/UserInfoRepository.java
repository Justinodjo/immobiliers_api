package com.immoservice.immobiliers.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.UserInfo;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    Optional<UserInfo> findByName(String userName);

    Optional<UserInfo> findByEmail(String email);

    Optional<UserInfo> findByNameOrEmail(String userName, String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserInfo u JOIN u.roles r WHERE r.nomRole = :roleName")
    List<UserInfo> findByRoleName(@Param("roleName") String roleName);

    List<UserInfo> findByAdresseIgnoreCase(String adresse);

    @Override
    Optional<UserInfo> findById(Integer id);

}
