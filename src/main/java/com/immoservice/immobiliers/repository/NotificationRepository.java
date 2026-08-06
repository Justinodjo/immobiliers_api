package com.immoservice.immobiliers.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.Notification;
import com.immoservice.immobiliers.entities.UserInfo;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUtilisateurAndNotificationsLu(UserInfo utilisateur, boolean notificationsLu);

    /**
     * Trouver toutes les notifications d'un utilisateur, triées par date
     * décroissante
     */
    List<Notification> findByUtilisateurOrderByDateNotificationDesc(UserInfo utilisateur);

    /**
     * Trouver les notifications non lues d'un utilisateur, triées par date
     * décroissante
     */
    List<Notification> findByUtilisateurAndNotificationsLuFalseOrderByDateNotificationDesc(UserInfo utilisateur);

    /**
     * Trouver les notifications non lues (sans tri particulier)
     */
    List<Notification> findByUtilisateurAndNotificationsLuFalse(UserInfo utilisateur);

    /**
     * Compter les notifications non lues d'un utilisateur
     */
    long countByUtilisateurAndNotificationsLuFalse(UserInfo utilisateur);

    /**
     * Trouver toutes les notifications d'un utilisateur
     */
    List<Notification> findByUtilisateur(UserInfo utilisateur);

    /**
     * Trouver les notifications antérieures à une date donnée
     */
    List<Notification> findByDateNotificationBefore(Date date);

    /**
     * Supprimer toutes les notifications d'un utilisateur
     */
    void deleteByUtilisateur(UserInfo utilisateur);

    /**
     * Supprimer les notifications antérieures à une date
     */
    void deleteByDateNotificationBefore(Date date);

}
