package com.immoservice.immobiliers.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.immoservice.immobiliers.entities.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByNom(String nom);
}
