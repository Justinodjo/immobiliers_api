package com.immoservice.immobiliers.services;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.immoservice.immobiliers.entities.Permission;
import com.immoservice.immobiliers.entities.UserInfo;

public class UserInfoDetails implements UserDetails {
    private final List<GrantedAuthority> authorities;
    private final UserInfo userInfo; // Ajout de l'objet UserInfo pour accéder à plus d'informations
    private final List<String> roles; // Liste des rôles séparée

    private final List<String> permissions; // Liste des permissions séparée

    public UserInfoDetails(UserInfo userInfo) {
        this.userInfo = userInfo;

        // Initialiser la liste des rôles
        this.roles = userInfo.getRoles().stream()
                .map(role -> "ROLE_" + role.getNomRole()) // Format ROLE_nom
                .collect(Collectors.toList());

        // Initialiser la liste des permissions
        this.permissions = userInfo.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream()) // Récupérer toutes les permissions
                .map(Permission::getNom) // Nom des permissions
                .collect(Collectors.toList());

        // Générer la liste des autorités combinées (rôles + permissions)
        this.authorities = Stream.concat(
                roles.stream().map(SimpleGrantedAuthority::new), // Convertir les rôles en GrantedAuthority
                permissions.stream().map(SimpleGrantedAuthority::new) // Convertir les permissions en GrantedAuthority
        ).collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return userInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return userInfo.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public List<String> getRoles() {
        return roles;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Récupérer les permissions directement de l'objet UserInfo
    public Set<Permission> getPermissions() {
        return userInfo.getPermissions(); // Récupérer les permissions directement depuis UserInfo
    }

    // Ajouter des méthodes pour obtenir plus d'informations sur l'utilisateur
    public UserInfo getUserInfo() {
        return userInfo;
    }

}