package com.immoservice.immobiliers.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.immoservice.immobiliers.entities.Notification;
import com.immoservice.immobiliers.entities.TypeNotifications;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.NotificationRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserInfoRepository userInfoRepository;

    /**
     * 🎨 Créer une notification personnalisée
     */
    @Transactional
    public Notification creerNotification(UserInfo utilisateur, String message,
            String titre, TypeNotifications type) {
        // Validation du type de notification
        if (type == null) {
            type = TypeNotifications.INFO;
        }

        // Créer et sauvegarder la notification
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUtilisateur(utilisateur);
        notification.setTypeNotifications(type);
        notification.setNotificationsLu(false);
        notification.setDateNotification(new Date());

        notification = notificationRepository.save(notification);

        // Préparer le payload pour WebSocket
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", notification.getId());
        payload.put("message", message);
        payload.put("contenu", message);
        payload.put("titre", titre != null ? titre : "Notification");
        payload.put("dateNotification", notification.getDateNotification());
        payload.put("typeNotifications", notification.getTypeNotifications());
        payload.put("notificationsLu", false);

        // Envoyer via WebSocket
        messagingTemplate.convertAndSendToUser(
                utilisateur.getId().toString(),
                "/queue/notifications",
                payload);

        return notification;
    }

    /**
     * 🧩 Crée une notification avec tous les paramètres possibles
     */
    @Transactional
    public Notification creerNotificationComplete(
            Integer userId,
            String titre,
            String message,
            TypeNotifications type,
            boolean notificationsLu,
            Date dateNotification,
            String priorite) {

        // Vérifier utilisateur
        UserInfo utilisateur = userInfoRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + userId));

        // Si la date n'est pas fournie → maintenant
        if (dateNotification == null) {
            dateNotification = new Date();
        }

        // Créer la notification
        Notification notification = new Notification();
        notification.setUtilisateur(utilisateur);
        notification.setTitre(titre != null ? titre : "Notification");
        notification.setMessage(message);
        notification.setTypeNotifications(type != null ? type : TypeNotifications.INFO);
        notification.setNotificationsLu(notificationsLu);
        notification.setDateNotification(dateNotification);
        notification.setPriorite(priorite); // si ton entité a "priorite"

        // Sauvegarde base
        Notification saved = notificationRepository.save(notification);

        // Envoi WebSocket
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", saved.getId());
        payload.put("titre", saved.getTitre());
        payload.put("message", saved.getMessage());
        payload.put("type", saved.getTypeNotifications());
        payload.put("priorite", saved.getPriorite());
        payload.put("date", saved.getDateNotification());
        payload.put("lu", saved.isNotificationsLu());

        messagingTemplate.convertAndSendToUser(
                utilisateur.getId().toString(),
                "/queue/notifications",
                payload);

        return saved;
    }

    /**
     * 
     * /**
     * 🔒 Notification privée (envoyée à un utilisateur spécifique)
     */
    public Notification envoyerNotificationPrivee(UserInfo user, String message) {
        Notification notification = new Notification();
        notification.setUtilisateur(user);
        notification.setMessage(message);
        notification.setDateNotification(new Date());
        notification.setNotificationsLu(false);

        Notification saved = notificationRepository.save(notification);

        // Canal privé : /user/{userId}/queue/notifications
        messagingTemplate.convertAndSendToUser(
                user.getId().toString(),
                "/queue/notifications",
                saved);

        return saved; // <- Retourne la notification
    }

    /**
     * 🌍 Notification globale (envoyée à tout le monde)
     */
    public Notification envoyerNotificationGlobale(String message) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setDateNotification(new Date());
        notification.setNotificationsLu(false);

        Notification saved = notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/notifications/global", saved);
        return saved;
    }

    /**
     * 🧩 Notification restreinte à un groupe d’utilisateurs (par rôle ou type)
     */
    public List<Notification> envoyerNotificationRestreinte(String role, String message) {
        List<UserInfo> utilisateurs = userInfoRepository.findByRoleName(role);
        List<Notification> notificationsEnvoyees = new ArrayList<>();

        for (UserInfo u : utilisateurs) {
            notificationsEnvoyees.add(envoyerNotificationPrivee(u, message));
        }

        return notificationsEnvoyees;
    }

    // 🔹 4️⃣ Notification restreinte à une zone géographique
    public List<Notification> envoyerNotificationParZone(String adresse, String message) {
        List<UserInfo> utilisateurs = userInfoRepository.findByAdresseIgnoreCase(adresse);
        List<Notification> notificationsEnvoyees = new ArrayList<>();
        for (UserInfo u : utilisateurs) {
            Notification notif = new Notification();
            notif.setUtilisateur(u);
            notif.setMessage(message);
            notif.setDateNotification(new Date());
            notif.setNotificationsLu(false);

            Notification saved = notificationRepository.save(notif);
            messagingTemplate.convertAndSend("/topic/notifications/" + u.getId(), saved);
            notificationsEnvoyees.add(saved);
        }
        return notificationsEnvoyees;
    }

    /**
     * 🎯 Notification orientée vers un contexte précis (ex : un bien ou contrat)
     */
    public List<Notification> envoyerNotificationOrientee(String contexte, List<UserInfo> destinataires,
            String message) {
        List<Notification> notificationsEnvoyees = new ArrayList<>();
        for (UserInfo user : destinataires) {
            Notification notif = new Notification();
            notif.setUtilisateur(user);
            notif.setMessage("[" + contexte + "] " + message);
            notif.setDateNotification(new Date());
            notif.setNotificationsLu(false);

            Notification saved = notificationRepository.save(notif);

            messagingTemplate.convertAndSend("/topic/notifications/" + user.getId(), saved);
            notificationsEnvoyees.add(saved);
        }
        return notificationsEnvoyees;
    }

    // ============================================
    // MÉTHODES DE LECTURE
    // ============================================

    /**
     * Récupérer toutes les notifications d'un utilisateur
     */
    public List<Notification> getNotificationsByUser(UserInfo utilisateur) {
        return notificationRepository.findByUtilisateurOrderByDateNotificationDesc(utilisateur);
    }

    /**
     * Récupérer les notifications non lues
     */
    public List<Notification> getUnreadNotifications(UserInfo utilisateur) {
        return notificationRepository.findByUtilisateurAndNotificationsLuFalseOrderByDateNotificationDesc(utilisateur);
    }

    /**
     * Compter les notifications non lues
     */
    public long getUnreadCount(UserInfo utilisateur) {
        return notificationRepository.countByUtilisateurAndNotificationsLuFalse(utilisateur);
    }

    public UserInfo findByUsernameOrEmail(String username) {
        return userInfoRepository.findByNameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));
    }
    // ============================================
    // MÉTHODES DE MISE À JOUR
    // ============================================

    /**
     * Marquer une notification comme lue
     */
    @Transactional
    public boolean markAsRead(Long notificationId, UserInfo utilisateur) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);

        if (notification != null && notification.getUtilisateur().getId().equals(utilisateur.getId())) {
            notification.setNotificationsLu(true);
            notificationRepository.save(notification);
            return true;
        }

        return false;
    }

    /**
     * Marquer toutes les notifications comme lues
     */
    @Transactional
    public int markAllAsRead(UserInfo utilisateur) {
        List<Notification> notifications = notificationRepository
                .findByUtilisateurAndNotificationsLuFalse(utilisateur);

        for (Notification notif : notifications) {
            notif.setNotificationsLu(true);
        }

        notificationRepository.saveAll(notifications);
        return notifications.size();
    }

    // ============================================
    // MÉTHODES DE SUPPRESSION
    // ============================================

    /**
     * Supprimer une notification
     */
    @Transactional
    public boolean deleteNotification(Long notificationId, UserInfo utilisateur) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);

        if (notification != null && notification.getUtilisateur().getId().equals(utilisateur.getId())) {
            notificationRepository.delete(notification);
            return true;
        }

        return false;
    }

    /**
     * Supprimer toutes les notifications d'un utilisateur
     */
    @Transactional
    public int deleteAllNotifications(UserInfo utilisateur) {
        List<Notification> notifications = notificationRepository.findByUtilisateur(utilisateur);
        int count = notifications.size();
        notificationRepository.deleteAll(notifications);
        return count;
    }

    /**
     * Supprimer les anciennes notifications (plus de 30 jours)
     */
    @Transactional
    public int deleteOldNotifications() {
        Date thirtyDaysAgo = new Date(System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000));
        List<Notification> oldNotifications = notificationRepository.findByDateNotificationBefore(thirtyDaysAgo);
        int count = oldNotifications.size();
        notificationRepository.deleteAll(oldNotifications);
        return count;
    }

}
