package com.immoservice.immobiliers.entities;

import java.util.Date;

import jakarta.persistence.CascadeType;

import jakarta.persistence.Entity;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
// @EntityListeners(AuditLogListener.class)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TypeNotifications typeNotifications;
    private String message;
    private Date dateNotification;
    private boolean notificationsLu;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "user_id") // Clé étrangère dans la table produits
    private UserInfo utilisateur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeNotifications getTypeNotifications() {
        return typeNotifications;
    }

    public void setTypeNotifications(TypeNotifications typeNotifications) {
        this.typeNotifications = typeNotifications;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDateNotification() {
        return dateNotification;
    }

    public void setDateNotification(Date dateNotification) {
        this.dateNotification = dateNotification;
    }

    public boolean isNotificationsLu() {
        return notificationsLu;
    }

    public void setNotificationsLu(boolean notificationsLu) {
        this.notificationsLu = notificationsLu;
    }

    public UserInfo getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(UserInfo utilisateur) {
        this.utilisateur = utilisateur;
    }

}