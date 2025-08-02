package com.immoservice.immobiliers.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.entities.AccessLog;
import com.immoservice.immobiliers.repository.AccessLogRepository;

import java.time.LocalDateTime;

@Service
public class AccessLogService {

    @Autowired
    private AccessLogRepository accessLogRepository;

    public void saveLog(String method, String uri, String contentType, String ipAddress, int statusCode) {
        AccessLog accessLog = new AccessLog(
                method,
                uri,
                contentType,
                ipAddress,
                statusCode,
                LocalDateTime.now());

        accessLogRepository.save(accessLog);
    }

}
