package com.immoservice.immobiliers.entities;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @CreationTimestamp // 🕒 Géré automatiquement par Hibernate
    @Column(updatable = false)
    private Date dateNotification;
    // 🔹 Priorité : HIGH / MEDIUM / LOW
    private String priorite;
    // 🔹 Titre affiché dans l'UI (optionnel)
    private String titre;
    // 🔹 Contexte : ex "BIEN #120", "CONTRAT", "SECURITE"
    private String contexte;
    @Column(nullable = false)
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

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getPriorite() {
        return priorite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public String getContexte() {
        return contexte;
    }

    public void setContexte(String contexte) {
        this.contexte = contexte;
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