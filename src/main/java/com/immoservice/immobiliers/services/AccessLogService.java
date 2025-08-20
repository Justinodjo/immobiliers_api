package com.immoservice.immobiliers.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.entities.AccessLog;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.AccessLogRepository;

import java.time.LocalDateTime;

@Service
public class AccessLogService {

    @Autowired
    private AccessLogRepository accessLogRepository;
    private static final Logger logger = LoggerFactory.getLogger(AccessLogService.class);

    public void saveLog(String method, String uri, String contentType, String ipAddress, int statusCode,
            UserInfo utilisateur) {
        AccessLog accessLog = new AccessLog(
                method,
                uri,
                contentType,
                ipAddress,
                statusCode,
                LocalDateTime.now());

        // Si utilisateur est null, on le laisse vide dans la BDD
        if (utilisateur != null) {
            accessLog.setUtilisateur(utilisateur);
            logger.info("Access log saved for user: {}", utilisateur.getName());
        } else {
            logger.info("Access log saved for anonymous user");
        }

        accessLogRepository.save(accessLog);
    }

}
