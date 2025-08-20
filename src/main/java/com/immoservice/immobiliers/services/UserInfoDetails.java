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

    public UserInfoDetails(UserInfo userInfo) {
        this.userInfo = userInfo; // Stocker l'objet UserInfo
        // Génération de la liste des autorités à partir des rôles + permissions
        this.authorities = userInfo.getRoles().stream()
                .flatMap(role -> {
                    // Rôle au format ROLE_nom
                    Stream<GrantedAuthority> roleAuthority = Stream
                            .of(new SimpleGrantedAuthority("ROLE_" + role.getNomRole()));

                    // Permissions associées à ce rôle
                    Stream<GrantedAuthority> permissionAuthorities = role.getPermissions().stream()
                            .map(permission -> new SimpleGrantedAuthority(permission.getNom()));

                    // Combine le rôle et les permissions
                    return Stream.concat(roleAuthority, permissionAuthorities);
                })
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

    // Récupérer les permissions directement de l'objet UserInfo
    public Set<Permission> getPermissions() {
        return userInfo.getPermissions(); // Récupérer les permissions directement depuis UserInfo
    }

    // Ajouter des méthodes pour obtenir plus d'informations sur l'utilisateur
    public UserInfo getUserInfo() {
        return userInfo;
    }

}