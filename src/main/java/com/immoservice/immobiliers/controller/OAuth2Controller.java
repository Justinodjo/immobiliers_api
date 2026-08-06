package com.immoservice.immobiliers.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.immoservice.immobiliers.entities.CustomOAuth2User;
import com.immoservice.immobiliers.entities.Role;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.response.AuthResponse;
import com.immoservice.immobiliers.services.JwtService;
import com.immoservice.immobiliers.services.RoleService;
import com.immoservice.immobiliers.services.UserInfoService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth/oauth2")
public class OAuth2Controller {

        @Autowired
        private JwtService jwtService;

        @Autowired
        private RoleService roleService;

        @Autowired
        private UserInfoService userInfoService;

        @GetMapping("/success")
        public AuthResponse success(OAuth2AuthenticationToken authentication) {
                if (authentication == null) {
                        throw new RuntimeException("Aucun utilisateur connecté via OAuth2");
                }

                CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
                UserInfo user = customUser.getUser();

                // 🔹 Si aucun rôle, renvoyer email pour que le frontend affiche la sélection de
                // rôle
                if (user.getRoles() == null || user.getRoles().isEmpty()) {
                        // Utilisation du constructeur spécifique pour la première connexion
                        return new AuthResponse(user.getEmail(), true);
                }
                // Récupération des rôles et permissions
                Collection<? extends GrantedAuthority> roles = user.getRoles().stream()
                                .flatMap(role -> {
                                        var roleAuthority = Stream
                                                        .of((GrantedAuthority) () -> "ROLE_" + role.getNomRole());
                                        var permissionAuthorities = role.getPermissions().stream()
                                                        .map(p -> (GrantedAuthority) () -> p.getNom());
                                        return Stream.concat(roleAuthority, permissionAuthorities);
                                }).collect(Collectors.toSet());

                List<String> permissions = user.getRoles().stream()
                                .flatMap(role -> role.getPermissions().stream())
                                .map(p -> p.getNom())
                                .distinct()
                                .collect(Collectors.toList());

                // Génération du token JWT
                String token = jwtService.generateToken(user.getName(), user.getEmail(), roles);
                // Générer le refresh token
                String refreshToken = jwtService.generateRefreshToken(user.getName(), user.getEmail(), roles);
                // Fichier principal et liste de fichiers
                Long fichierPrincipal = user.getFichier() != null ? user.getFichier().getId() : null;
                List<Long> fichiers = user.getFichiers().stream()
                                .map(f -> f.getId())
                                .collect(Collectors.toList());

                // Retourner AuthResponse comme pour login classique
                return new AuthResponse(
                                user.getId(),
                                token,
                                refreshToken,
                                user.getName(),
                                user.getSecondName(),
                                user.getNumero(),
                                user.getEmail(),
                                fichierPrincipal,
                                fichiers,
                                roles,
                                permissions, false);
        }

        @GetMapping("/failure")
        public String failure() {
                return "Erreur de connexion OAuth2";
        }

        @PostMapping("/complete-oauth2")
        public ResponseEntity<Map<String, String>> completeOauth2(
                        @RequestParam String email,
                        @RequestParam String roleName, HttpServletResponse response) throws IOException {

                // 1️⃣ Récupérer l'utilisateur
                UserInfo user = userInfoService.findUserByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

                // 2️⃣ Vérifier si l'utilisateur est vraiment nouveau
                if (user.getRoles() != null && !user.getRoles().isEmpty()) {

                        return ResponseEntity.badRequest().body(Map.of("error", "Utilisateur déjà configuré"));
                }
                // 2️⃣ Ajouter le rôle choisi
                Role role = roleService.findByName(roleName);
                if (role == null) {
                        return ResponseEntity.badRequest().body(Map.of("error", "Rôle invalide"));
                }

                user.getRoles().add(role);
                userInfoService.saveUser(user);

                // 3️⃣ Générer JWT et cookie
                Collection<? extends GrantedAuthority> authorities = user.getRoles().stream()
                                .flatMap(r -> {
                                        var roleAuthority = Stream
                                                        .of((GrantedAuthority) () -> "ROLE_" + r.getNomRole());
                                        var permissionAuthorities = r.getPermissions().stream()
                                                        .map(p -> (GrantedAuthority) () -> p.getNom());
                                        return Stream.concat(roleAuthority, permissionAuthorities);
                                })
                                .collect(Collectors.toSet());

                String token = jwtService.generateToken(user.getName(), user.getEmail(), authorities);

                boolean isHttp = false; // HTTPS normalement
                String cookieValue = "token=" + token
                                + (isHttp ? "" : "; Secure")
                                + "; Path=/"
                                + "; Max-Age=" + (7 * 24 * 60 * 60)
                                + "; Secure; SameSite=None; Domain=localhost";

                response.setHeader("Set-Cookie", cookieValue);

                // 5️⃣ Déterminer la redirection
                String redirectUrl = switch (roleName) {
                        case "LOCATAIRE_ROLES" -> "/locataire/locataire_dashboard";
                        case "PROPRIETAIRE_ROLES" -> "/proprietaire/proprietaire_dashboard";
                        case "ADMIN_ROLES" -> "/admin/admin_dashboard";
                        case "MANAGER_ROLES" -> "/manager/manager_dashboard";
                        case "SUPER_ADMIN_ROLES" -> "/master/master_dashboard";
                        default -> "/home";
                };

                // 4️⃣ Redirection selon le rôle
                return ResponseEntity.ok(Map.of("token", token, "redirectUrl", redirectUrl, "roleName", roleName));
        }

        private void collect(Collector<Object, ?, List<Object>> toList) {
                throw new UnsupportedOperationException("Not supported yet.");
        }
}
