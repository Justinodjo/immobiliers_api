package com.immoservice.immobiliers.services;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.entities.AccessLog;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.AccessLogRepository;

@Service
public class AccessLogService {

    private final AccessLogRepository accessLogRepository;
    private static final Logger logger = LoggerFactory.getLogger(AccessLogService.class);

    public AccessLogService(AccessLogRepository accessLogRepository) {
        this.accessLogRepository = accessLogRepository;
    }

    /**
     * Sauvegarde un log d'accès dans la base de données.
     *
     * @param method      Méthode HTTP utilisée (GET, POST, etc.)
     * @param uri         URI accédée
     * @param contentType Type de contenu de la requête (JSON, XML...)
     * @param ipAddress   Adresse IP du client
     * @param statusCode  Code de réponse HTTP
     * @param utilisateur Utilisateur connecté (peut être null pour une requête
     *                    anonyme)
     */
    public void saveLog(String method, String uri, String contentType, String ipAddress, int statusCode,
            UserInfo utilisateur) {
        AccessLog accessLog = new AccessLog(
                method,
                uri,
                contentType,
                ipAddress,
                statusCode,
                LocalDateTime.now());

        if (utilisateur != null) {
            accessLog.setUtilisateur(utilisateur);
            logger.info("✅ Access log enregistré pour l'utilisateur : {}", utilisateur.getName());
        } else {
            logger.info("✅ Access log enregistré pour un utilisateur anonyme");
        }

        accessLogRepository.save(accessLog);
    }

    public void logAction(String uri, String method, String description, UserInfo utilisateur, int statusCode) {
        AccessLog log = new AccessLog();
        log.setUri(uri);
        log.setMethod(method);
        log.setContentType("application/internal");
        log.setIpAddress("internal");
        log.setResponseStatus(statusCode);
        log.setAccessTime(LocalDateTime.now());
        log.setUtilisateur(utilisateur);

        accessLogRepository.save(log);
    }
}
