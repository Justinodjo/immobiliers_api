package com.immoservice.immobiliers.services;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.immoservice.immobiliers.entities.UserInfo;

public class UserInfoDetails implements UserDetails {
    private final List<GrantedAuthority> authorities;
    private final UserInfo userInfo; // Ajout de l'objet UserInfo pour accéder à plus d'informations

    public UserInfoDetails(UserInfo userInfo) {
        this.userInfo = userInfo; // Stocker l'objet UserInfo
        this.authorities = userInfo.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getNomRole()))
                .collect(Collectors.toList());
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
        return userInfo.getName();
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

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Ajouter des méthodes pour obtenir plus d'informations sur l'utilisateur
    public UserInfo getUserInfo() {
        return userInfo;
    }
}