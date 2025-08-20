package com.immoservice.immobiliers.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
// @Builder // Utilisation de Lombok pour générer le constructeur, les getters
// et setters
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ipAddress;
    private String uri;
    private String method;
    private String contentType;

    private int responseStatus;
    private LocalDateTime accessTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInfo utilisateur;

    // Constructeurs
    public AccessLog() {
        this.accessTime = LocalDateTime.now();
    }

    public AccessLog(String method, String uri, String contentType, String ipAddress, int responseStatus,
            LocalDateTime accessTime) {
        this.method = method;
        this.uri = uri;
        this.contentType = contentType;
        this.ipAddress = ipAddress;
        this.responseStatus = responseStatus;
        this.accessTime = accessTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public LocalDateTime getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(LocalDateTime accessTime) {
        this.accessTime = accessTime;
    }

    // Getters et setters
    // ...

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public UserInfo getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(UserInfo utilisateur) {
        this.utilisateur = utilisateur;
    }
}
