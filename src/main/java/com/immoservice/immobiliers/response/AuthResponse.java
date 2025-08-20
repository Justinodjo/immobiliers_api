package com.immoservice.immobiliers.response;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

public class AuthResponse {
    private Integer id;
    private String token;
    private String userName;
    private String secondName;
    private String numero;
    private String email;
    private String fichier;
    private List<String> fichiers;
    private Collection<? extends GrantedAuthority> roles;
    private List<String> permissions;

    // Constructeurs, getters et setters
    public AuthResponse(Integer id, String token, String userName, String secondName, String numero, String email,
            String fichier,
            List<String> fichiers, Collection<? extends GrantedAuthority> roles, List<String> permissions) {
        this.id = id;
        this.token = token;
        this.userName = userName;
        this.secondName = secondName;
        this.numero = numero;
        this.email = email;
        this.roles = roles;
        this.fichier = fichier;
        this.fichiers = fichiers;
        this.permissions = permissions;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFichier() {
        return fichier;
    }

    public void setFichier(String fichier) {
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

    public List<String> getFichiers() {
        return fichiers;
    }

    public void setFichiers(List<String> fichiers) {
        this.fichiers = fichiers;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
