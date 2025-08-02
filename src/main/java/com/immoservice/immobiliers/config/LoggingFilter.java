package com.immoservice.immobiliers.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.immoservice.immobiliers.entities.AccessLog;
import com.immoservice.immobiliers.repository.AccessLogRepository;
import com.immoservice.immobiliers.services.AccessLogService;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Autowired
    private AccessLogService accessLogService;

    @Override
    @SuppressWarnings("null")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Obtenir l'adresse IP du client
        String ipAddress = getClientIp(request);
        String uri = request.getRequestURI();
        String contentType = request.getContentType();
        // Vérifier si le contentType est null pour éviter les NullPointerException
        if (contentType == null) {
            contentType = "N/A"; // ou une valeur par défaut
        }
        // Obtenir la méthode de la requête
        String method = request.getMethod();
        // Log request details
        System.out.println("=== Nouvelle requête reçue ===");
        System.out.println("📡 Adresse IP : " + ipAddress);
        System.out.println("🔀 Request Method: " + request.getMethod());
        System.out.println("📍 Request URI: " + request.getRequestURI());
        System.out.println("📄 Content-Type: " + request.getContentType());

        try {
            // Enregistrer dans la base de données

            int status = response.getStatus();

            // Stocker dans la BDD
            accessLogService.saveLog(method, uri, contentType, ipAddress, status);

            filterChain.doFilter(request, response);
        } catch (java.io.IOException | ServletException e) {

            e.printStackTrace();
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
