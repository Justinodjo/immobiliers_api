package com.immoservice.immobiliers.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.immoservice.immobiliers.entities.Permission;
import com.immoservice.immobiliers.repository.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission getById(Long id) {
        return permissionRepository.findById(id).orElse(null);
    }

    public List<Permission> getAll() {
        return permissionRepository.findAll();
    }

    public Permission create(Permission permission) {
        return permissionRepository.save(permission);
    }

    public Permission update(Long id, Permission permission) {
        permission.setId(id);
        return permissionRepository.save(permission);
    }

    public void delete(Long id) {
        permissionRepository.deleteById(id);
    }
}
