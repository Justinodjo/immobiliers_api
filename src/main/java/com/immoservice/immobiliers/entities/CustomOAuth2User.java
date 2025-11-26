package com.immoservice.immobiliers.entities;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2User implements OAuth2User {

    private OAuth2User oauth2User;
    private UserInfo user;

    public CustomOAuth2User(UserInfo user, OAuth2User oauth2User) {
        this.user = user;
        this.oauth2User = oauth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Récupérer les autorités de la BDD (rôles + permissions)
        Set<GrantedAuthority> dbAuthorities = user.getRoles().stream()
                .flatMap(role -> {
                    // Autorité du rôle
                    SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(role.getNomRole());

                    // Autorités des permissions
                    List<SimpleGrantedAuthority> permissionAuthorities = role.getPermissions().stream()
                            .map(p -> new SimpleGrantedAuthority(p.getNom()))
                            .collect(Collectors.toList());

                    return Stream.concat(Stream.of(roleAuthority), permissionAuthorities.stream());
                })
                .collect(Collectors.toSet());

        // Fusionner avec les autorités par défaut de Google
        dbAuthorities.addAll(oauth2User.getAuthorities());

        return dbAuthorities;
    }

    @Override
    public String getName() {
        return user.getName() != null ? user.getName() : oauth2User.getAttribute("name");
    }

    public String getEmail() {
        return user.getEmail() != null ? user.getEmail() : oauth2User.getAttribute("email");
    }

    public UserInfo getUser() {
        return user;
    }

    // ✅ Ajouté pour pouvoir l’utiliser dans le SuccessHandler
    public List<String> getRoles() {
        return user.getRoles().stream()
                .map(r -> r.getNomRole())
                .collect(Collectors.toList());
    }
}
