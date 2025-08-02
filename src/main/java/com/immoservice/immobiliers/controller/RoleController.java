package com.immoservice.immobiliers.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.immoservice.immobiliers.entities.Role;
import com.immoservice.immobiliers.services.RoleService;

import java.util.List;

@RestController
@RequestMapping("api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // Création d'un nouveau rôle
    @PostMapping("creer")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role createdRole = roleService.createRole(role);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }

    // Récupération de la liste des rôles
    @GetMapping("liste")
    public ResponseEntity<List<Role>> listRoles() {
        List<Role> roles = roleService.listRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    // Récupération d'un rôle par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") Long idRole) {
        Role role = roleService.findRoleById(idRole);
        if (role != null) {
            return new ResponseEntity<>(role, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Mise à jour d'un rôle
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable("id") Long idRole, @RequestBody Role role) {
        Role updatedRole = roleService.updateRole(idRole, role);
        if (updatedRole != null) {
            return new ResponseEntity<>(updatedRole, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Suppression d'un rôle
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") Long idRole) {
        roleService.deleteRole(idRole);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
