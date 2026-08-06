package com.immoservice.immobiliers.response;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

public class AuthResponse {
    private Integer id;
    private String token;
    private String refreshToken;
    private String userName;
    private String secondName;
    private String numero;
    private String email;
    private Long fichier;
    private List<Long> fichiers;
    private Collection<? extends GrantedAuthority> roles;
    private List<String> permissions;
    private boolean needsRoleSelection;

    // Constructeurs, getters et setters
    public AuthResponse(Integer id, String token, String refreshToken, String userName, String secondName,
            String numero, String email,
            Long fichier, List<Long> fichiers, Collection<? extends GrantedAuthority> roles,
            List<String> permissions, boolean needsRoleSelection) {
        this.id = id;
        this.token = token;
        this.refreshToken = refreshToken;
        this.userName = userName;
        this.secondName = secondName;
        this.numero = numero;
        this.email = email;
        this.fichier = fichier;
        this.fichiers = fichiers;
        this.roles = roles;
        this.permissions = permissions;
        this.needsRoleSelection = needsRoleSelection;
    }

    public AuthResponse(String email, boolean needsRoleSelection) {
        this.email = email;
        this.needsRoleSelection = needsRoleSelection;
    }

    public AuthResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getFichier() {
        return fichier;
    }

    public void setFichier(Long fichier) {
        this.fichier = fichier;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Collection<? extends GrantedAuthority> getRoles() {
        return roles;
    }

    public void setRoles(Collection<? extends GrantedAuthority> roles) {
        this.roles = roles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Long> getFichiers() {
        return fichiers;
    }

    public void setFichiers(List<Long> fichiers) {
        this.fichiers = fichiers;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public boolean isNeedsRoleSelection() {
        return needsRoleSelection;
    }

    public void setNeedsRoleSelection(boolean needsRoleSelection) {
        this.needsRoleSelection = needsRoleSelection;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
