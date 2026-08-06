package com.immoservice.immobiliers.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.immoservice.immobiliers.services.PasswordResetService;

@Component
public class ScheduledTasks {

    @Autowired
    private PasswordResetService passwordResetService;

    /**
     * Nettoie les tokens expirés toutes les heures
     */
    @Scheduled(cron = "0 0 * * * *") // Toutes les heures
    public void cleanExpiredTokens() {
        passwordResetService.cleanExpiredTokens();
    }
}
