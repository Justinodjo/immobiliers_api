package com.immoservice.immobiliers.services;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.entities.Notification;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.NotificationRepository;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public void envoyerNotifications(UserInfo user, String message) {
        Notification notification = new Notification();

        notification.setUtilisateur(user);
        notification.setMessage(message);
        notification.setDateNotification(new Date());

        notificationRepository.save(notification);
    }
}
