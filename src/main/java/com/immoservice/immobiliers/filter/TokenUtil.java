package com.immoservice.immobiliers.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.immoservice.immobiliers.services.JwtService;

@Component
public class TokenUtil {

    private final Logger logger = LoggerFactory.getLogger(TokenUtil.class);

    @Autowired
    private JwtService jwtService;

    @SuppressWarnings("null")
    public String getUserIdByToken(HttpHeaders headers, String secret) {
        logger.debug("Token Util Start");

        if (headers == null || headers.get("Authorization") == null || headers.get("Authorization").isEmpty()) {
            logger.error("Authorization header is missing or empty");
            return null;
        }

        String token = headers.get("Authorization").get(0);
        if (token == null || token.isEmpty()) {
            logger.error("Token is missing or empty");
            return null;
        }

        logger.debug("Token Util token: {}", token);

        try {
            String userId = jwtService.extractUserName(token);
            logger.debug("Token Util userId: {}", userId);

            return userId;
        } catch (Exception e) {
            logger.error("Error parsing token", e);
            return null;
        }
    }
}
