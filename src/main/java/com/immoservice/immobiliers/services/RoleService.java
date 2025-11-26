package com.immoservice.immobiliers.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.entities.Role;
import com.immoservice.immobiliers.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    public Role findRoleById(Long idRole) {
        return roleRepository.findById(idRole).orElse(null);
    }

    public Role updateRole(Long idRole, Role role) {
        if (roleRepository.existsById(idRole)) {
            role.setIdRole(idRole);
            return roleRepository.save(role);
        }
        return null;
    }

    public void deleteRole(Long idRole) {
        roleRepository.deleteById(idRole);
    }

    public Role findByName(String nomRole) {
        return roleRepository.findByNomRole(nomRole)
                .orElseThrow(() -> new IllegalArgumentException("Rôle introuvable : " + nomRole));
    }
}
