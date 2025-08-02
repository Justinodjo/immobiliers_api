package com.immoservice.immobiliers.config;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class CustomFirewallFilter implements Filter {

    private Set<String> blockedIps = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Chargement de la liste des IPs bloquées depuis une source externe (ex:
        // fichier ou DB)
        blockedIps.add("192.168.1.1"); // Exemple d'IP bloquée
        // Ajouter d'autres IPs si nécessaire
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String clientIp = httpRequest.getRemoteAddr();
        if (isBlockedIp(clientIp)) {
            throw new ServletException("Access denied for this IP address: " + clientIp);
        }

        chain.doFilter(request, response);
    }

    private boolean isBlockedIp(String ip) {
        return blockedIps.contains(ip); // Vérifie si l'IP est dans la liste bloquée
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
