package com.immoservice.immobiliers.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.immoservice.immobiliers.entities.CustomOAuth2User;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.services.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

        private final JwtService jwtService;

        @Autowired
        public CustomOAuth2SuccessHandler(JwtService jwtService) {
                this.jwtService = jwtService;
        }

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request,
                        HttpServletResponse response,
                        Authentication authentication) throws IOException {
                // ✅ Caster en CustomOAuth2User pour accéder aux rôles et permissions
                CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
                String email = oAuth2User.getAttribute("email");
                // ✅ Récupération de l'utilisateur depuis ton CustomOAuth2User
                UserInfo user = oAuth2User.getUser();
                // Vérifie si l'utilisateur a déjà un rôle
                // 🔹 Si aucun rôle, rediriger vers le choix de rôle
                if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
                        String redirectUrl = "https://dev.mainsdorplus.com/select-role?email="
                                        + java.net.URLEncoder.encode(email, StandardCharsets.UTF_8);
                        response.sendRedirect(redirectUrl);
                        return;
                }
                // Sinon, générer le JWT et rediriger selon le rôle
                generateJwtAndRedirect(user, request, response);
        }

        public void generateJwtAndRedirect(UserInfo user, HttpServletRequest request, HttpServletResponse response)
                        throws IOException {
                Collection<GrantedAuthority> authorities = user.getRoles().stream()
                                .flatMap(role -> {
                                        // Autorité du rôle
                                        var roleAuthority = new SimpleGrantedAuthority("ROLE_" + role.getNomRole());

                                        // Autorités des permissions
                                        var permissionAuthorities = role.getPermissions().stream()
                                                        .map(permission -> new SimpleGrantedAuthority(
                                                                        permission.getNom()));

                                        // Fusion rôle + permissions
                                        return Stream.concat(Stream.of(roleAuthority), permissionAuthorities);
                                })
                                .collect(Collectors.toSet());
                // Génération du JWT interne
                // Générer le token JWT
                String token = jwtService.generateToken(user.getName(), user.getEmail(), authorities);

                boolean isHttp = request.getScheme().equals("http");
                // ✅ Créer le cookie via header
                String cookieValue = "token=" + token
                                + (isHttp ? "" : "; Secure")
                                + "; Path=/"
                                + "; Max-Age=" + (7 * 24 * 60 * 60)
                                + "; SameSite=Lax";

                response.setHeader("Set-Cookie", cookieValue);

                Map<String, String> roleRedirects = Map.of(
                                "LOCATAIRE_ROLES", "https://dev.mainsdorplus.com/Locataire/locataire_dashboard",
                                "PROPRIETAIRE_ROLES", "https://dev.mainsdorplus.com/proprietaire/proprietaire_dashboard",
                                "ADMIN_ROLES", "https://dev.mainsdorplus.com/admin/admin_dashboard",
                                "MANAGER_ROLES", "https://dev.mainsdorplus.com/manager/manager_dashboard",
                                "SUPER_ADMIN_ROLES", "https://dev.mainsdorplus.com/master/master_dashboard");

                String role = user.getRoles().iterator().next().getNomRole();
                String redirectUrl = roleRedirects.getOrDefault(role, "https://dev.mainsdorplus.com/home");

                response.sendRedirect(redirectUrl);

        }
}
