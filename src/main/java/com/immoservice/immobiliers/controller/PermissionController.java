package com.immoservice.immobiliers.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.immoservice.immobiliers.entities.Permission;
import com.immoservice.immobiliers.services.PermissionService;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public List<Permission> getAll() {
        return permissionService.getAll();
    }

    @PostMapping
    public Permission create(@RequestBody Permission permission) {
        return permissionService.create(permission);
    }

    @PutMapping("/{id}")
    public Permission update(@PathVariable Long id, @RequestBody Permission permission) {
        return permissionService.update(id, permission);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        permissionService.delete(id);
    }

    @GetMapping("/{id}")
    public Permission getById(@PathVariable Long id) {
        return permissionService.getById(id);
    }
}
