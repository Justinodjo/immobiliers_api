package com.immoservice.immobiliers.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.immoservice.immobiliers.services.JwtService;
import com.immoservice.immobiliers.services.UserInfoService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserInfoService userInfoService;

    private static final List<String> PUBLIC_URLS = List.of(
            "/auth/login",
            "/oauth2/",
            "/auth/addUser",
            "/auth/oauth2/failure",
            "/auth/oauth2/success",
            "/auth/oauth2/",
            // ===== SWAGGER / OpenAPI (AJOUTÉ) =====
            "/v3/api-docs",
            "/v3/api-docs/",
            "/swagger-ui",
            "/swagger-ui/",
            "/swagger-ui.html",
            "/swagger-resources",
            "/swagger-resources/",
            "/configuration/ui",
            "/configuration/security",
            "/webjars/",
            "/public-apis/",
            // ===== H2 Console =====
            "/h2-console",
            // ===== Biens publics =====
            "/biens/all",
            "/biens/search",
            "/biens/get/",
            "/biens/vues/",
            "/biens/noter/",
            "/biens/like/",
            "/biens/by-type",
            // ===== Types & Details =====
            "/typeBiens/",
            "/details_biens/",
            "/api/sous-details-biens/",
            // ===== Password Reset =====
            "/api/reset-password",
            "/api/request-password-reset",
            "/api/password-reset/",
            // ===== Contact & Avis =====
            "/contact/send",
            "/api/avis/",
            // ===== Localisation =====
            "/api/pays/",
            "/api/departement/",
            "/api/commune/",
            "/api/arrondissement/",
            "/api/quartiers/",
            // ===== Consentement =====
            "/consentement/",
            // ===== Interventions =====
            "/api/interventions",
            "/enums/",
            // ===== Offres =====
            "/api/offres/",
            // ===== Files =====
            "/fichier/download/",
            "/tfe/uploads/"

    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();
        boolean isPublic = PUBLIC_URLS.stream().anyMatch(path::startsWith);
        if (isPublic) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = null;
        String email = null;
        // ✅ 1️⃣ Essayer via le header Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // ✅ 2️⃣ Sinon, essayer via le cookie "token"
        if (token == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // ✅ 3️⃣ Vérifier que le token ressemble bien à un JWT
        if (token != null && token.chars().filter(ch -> ch == '.').count() == 2) {
            try {
                email = jwtService.extractEmail(token);
            } catch (Exception e) {
                System.err.println("❌ Erreur extraction username depuis token : " + e.getMessage());
            }
        } else if (token != null) {
            System.err.println("⚠️ Token non JWT détecté, ignoré : " + token);
        }

        // ✅ 4️⃣ Authentifier l’utilisateur si pas déjà dans le contexte
        if (email != null) {

            // ⚠️ Éviter de réauthentifier si déjà authentifié
            if (SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails;
            try {
                userDetails = userInfoService.loadUserByUsername(email);
            } catch (UsernameNotFoundException e) {
                System.err.println("⚠️ Aucun utilisateur trouvé pour : " + email);
                filterChain.doFilter(request, response);
                return;
            }

            if (jwtService.validateToken(token, userDetails)) {
                List<String> roles = jwtService.extractRoles(token);
                List<String> permissions = jwtService.extractPermissions(token);

                List<GrantedAuthority> authorities = new java.util.ArrayList<>();
                if (roles != null)
                    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                if (permissions != null)
                    permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.err.println("❌ Token JWT invalide pour l’utilisateur : " + email);
            }
        }

        filterChain.doFilter(request, response);
    }
}
