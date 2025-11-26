package com.immoservice.immobiliers.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.services.AccessLogService;
import com.immoservice.immobiliers.services.UserInfoDetails;

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
        String contentType = request.getContentType() != null ? request.getContentType() : "N/A";
        // Obtenir la méthode de la requête
        String method = request.getMethod();
        // Récupérer l'utilisateur connecté
        UserInfo utilisateur = getCurrentUser();
        // Log request details
        System.out.println("=== Nouvelle requête reçue ===");
        System.out.println("📡 Adresse IP : " + ipAddress);
        System.out.println("🔀 Request Method: " + request.getMethod());
        System.out.println("📍 Request URI: " + request.getRequestURI());
        System.out.println("📄 Content-Type: " + request.getContentType());
        System.out.println("👤 Utilisateur : " + (utilisateur != null ? utilisateur.getName() : "anonyme"));

        filterChain.doFilter(request, response);
        // Enregistrer dans la base de données

        int status = response.getStatus();

        // Stocker dans la BDD
        accessLogService.saveLog(method, uri, contentType, ipAddress, status, utilisateur);
    }

    // 👉 Cette méthode permet d’exclure certaines routes du logging
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        // Exclure Swagger et OpenAPI
        return path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/configuration/ui") ||
                path.startsWith("/configuration/security") ||
                path.startsWith("/webjars/") ||
                path.equals("/swagger-ui.html") ||
                // Autres exclusions
                path.startsWith("/actuator") ||
                path.equals("/favicon.ico");
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private UserInfo getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Object principal = auth.getPrincipal();
            if (principal instanceof UserInfoDetails details) {
                return details.getUserInfo();
            } else if (principal instanceof UserInfo info) {
                return info;
            }
        }
        return null;
    }
}
