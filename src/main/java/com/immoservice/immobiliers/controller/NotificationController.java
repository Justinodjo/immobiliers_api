package com.immoservice.immobiliers.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.immoservice.immobiliers.entities.Notification;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.services.NotificationService;
import com.immoservice.immobiliers.services.UserInfoService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserInfoService userInfoService; // <- ajouté

    @PostMapping("/global")
    public void sendGlobal(@RequestBody Map<String, String> body) {
        notificationService.envoyerNotificationGlobale(body.get("message"));
    }

    /** Notification privée envoyée depuis le current user */
    @PostMapping("/current-user")
    public ResponseEntity<?> sendFromCurrentUser(@RequestBody Map<String, String> body, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    String jwt = cookie.getValue();
                    UserInfo currentUser = userInfoService.getUserFromToken(jwt);
                    if (currentUser != null) {
                        // ⚡ Ne pas assigner à UserInfo
                        notificationService.envoyerNotificationPrivee(currentUser, body.get("message"));
                        return ResponseEntity.ok("Notification envoyée !");
                    }
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non connecté");
    }

    /**
     * Récupérer toutes les notifications de l'utilisateur connecté
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUserNotifications(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Non authentifié"));
            }

            String username = authentication.getName();
            UserInfo user = notificationService.findByUsernameOrEmail(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Utilisateur non trouvé"));
            }

            List<Notification> notifications = notificationService.getNotificationsByUser(user);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Erreur: " + e.getMessage()));
        }
    }

    /**
     * Récupérer uniquement les notifications non lues
     */
    @GetMapping("/user/unread")
    public ResponseEntity<?> getUnreadNotifications(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Non authentifié"));
            }

            String username = authentication.getName();
            UserInfo user = notificationService.findByUsernameOrEmail(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Utilisateur non trouvé"));
            }

            List<Notification> notifications = notificationService.getUnreadNotifications(user);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Erreur: " + e.getMessage()));
        }
    }

    /**
     * Marquer une notification comme lue
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Non authentifié"));
            }

            String username = authentication.getName();
            UserInfo user = notificationService.findByUsernameOrEmail(username);

            boolean success = notificationService.markAsRead(id, user);

            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Notification marquée comme lue"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Notification non trouvée"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Erreur: " + e.getMessage()));
        }
    }

    /**
     * Marquer toutes les notifications comme lues
     */
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Non authentifié"));
            }

            String username = authentication.getName();
            UserInfo user = notificationService.findByUsernameOrEmail(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Utilisateur non trouvé"));
            }

            int count = notificationService.markAllAsRead(user);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", count + " notifications marquées comme lues",
                    "count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Erreur: " + e.getMessage()));
        }
    }

    /**
     * Supprimer une notification
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Non authentifié"));
            }

            String username = authentication.getName();
            UserInfo user = notificationService.findByUsernameOrEmail(username);

            boolean success = notificationService.deleteNotification(id, user);

            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Notification supprimée"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Notification non trouvée"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Erreur: " + e.getMessage()));
        }
    }

    /**
     * Supprimer toutes les notifications de l'utilisateur
     */
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearAllNotifications(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Non authentifié"));
            }

            String username = authentication.getName();
            UserInfo user = notificationService.findByUsernameOrEmail(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Utilisateur non trouvé"));
            }

            int count = notificationService.deleteAllNotifications(user);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", count + " notifications supprimées",
                    "count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Erreur: " + e.getMessage()));
        }
    }

    /**
     * Obtenir le nombre de notifications non lues
     */
    @GetMapping("/user/unread/count")
    public ResponseEntity<?> getUnreadCount(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Non authentifié"));
            }

            String username = authentication.getName();
            UserInfo user = notificationService.findByUsernameOrEmail(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Utilisateur non trouvé"));
            }

            long count = notificationService.getUnreadCount(user);
            return ResponseEntity.ok(Map.of("success", true, "count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Erreur: " + e.getMessage()));
        }
    }
}
