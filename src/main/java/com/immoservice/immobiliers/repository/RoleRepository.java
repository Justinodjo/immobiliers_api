package com.immoservice.immobiliers.repository;

import com.immoservice.immobiliers.entities.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
     Optional<Role> findByNomRole(String nomRole);
     // ✅ Nouvelle méthode pour récupérer une liste de rôles à partir de leurs noms
    List<Role> findByNomRoleIn(List<String> nomRoles);
}
