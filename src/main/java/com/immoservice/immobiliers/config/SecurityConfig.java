package com.immoservice.immobiliers.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.immoservice.immobiliers.filter.JwtFilter;
import com.immoservice.immobiliers.services.CustomOAuth2UserService;
import com.immoservice.immobiliers.services.UserInfoService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Autowired
        private JwtFilter jwtFilter;

        @Autowired
        private RateLimitingFilter rateLimitingFilter;

        @Autowired
        private CustomFirewallFilter customFirewallFilter;

        @Autowired
        private LoggingFilter loggingFilter;

        @Autowired
        private CustomOAuth2UserService customOAuth2UserService;

        @Autowired
        private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

        @Autowired
        private OAuth2CorsBypassFilter oAuth2CorsBypassFilter;

        @Autowired
        private UserInfoService userDetailsService; // injecte le bean existant
        @Autowired
        private PasswordEncoder passwordEncoder;

        private final String[] SWAGGER_WHITELIST = {
                        // ===== SWAGGER / OpenAPI =====
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/swagger-resources",
                        "/configuration/ui",
                        "/configuration/security",
                        "/webjars/**",
                        "/public-apis/**",
                        // Auth endpoints
                        "/oauth2/**",
                        "/auth/oauth2/**",
                        "/auth/welcome",
                        "/auth/addUser",
                        "/auth/login",
                        "/auth/logout",
                        "/auth/me",
                        "/auth/getUsers/{id}",
                        "/auth/getUsers",
                        "/fichier/download/{id}",
                        "/fichier//download/{nom}", "/auth/getUsers/{id}",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/auth/getUsers",
                        "/auth/getAdmins",
                        "/auth/getManagers",
                        "/auth/updateUser/{id}",
                        "/api/roles/liste",
                        "/auth/assign-role",
                        "/auth/getUser/{id}",
                        "/auth/oauth2/complete-oauth2",
                        "/fichier/add",
                        "/tfe/uploads/**",
                        "/auth/delete/{id}",
                        "/auth/updateUser/{id}",
                        "/api/paiement/confirm",
                        "/api/roles/creer",
                        "/auth/refresh-token",
                        "/biens/all",
                        "/biens/search",
                        "/biens/search**",
                        "/biens/get/{id}",
                        "/biens/vues/{id}",
                        "/biens/noter/{id}",
                        "/biens/{id}/like",
                        "/biens/like/**",
                        "/biens/by-type",
                        "/api/suggestions/create",
                        "/api/suggestions/**",
                        "/typeBiens/get/{id}",
                        "/typeBiens/all",
                        "/details_biens/all",
                        "/typeBiens/**",
                        "/api/sous-details-biens/add",
                        "/details_biens/get/{id}",
                        "/api/sous-details-biens/{id}",
                        // Password reset
                        "/api/reset-password",
                        "/api/request-password-reset",

                        // Consentement
                        "/consentement/add",
                        "/consentement/all",
                        "/consentement/get/{id}",
                        "/consentement/delete/{id}",
                        "/consentement/by-user/{userId}",

                        // Divers
                        "/contact/send",
                        "/tfe/uploads/**",

                        // Avis
                        "/api/avis/add",
                        "/api/avis/all",
                        "/api/avis/bien/{id}",

                        // Pays
                        "/api/pays/ajouter",
                        "/api/pays/liste",
                        "/api/pays/{id}",
                        "/api/pays/modifier/{id}",
                        "/api/pays/supprimer/{id}",

                        // Departements
                        "/api/departement/liste_departements",
                        "/api/departement/{idDepartement}",
                        "/api/departement/create_departement",
                        "/api/departement/update/{idDepartement}",
                        "/api/departement/delete/{idDepartement}",
                        "/api/departement/by-pays/{paysId}",

                        // Commune
                        "/api/commune/create_commune",
                        "/api/commune/liste_Communes",
                        "/api/commune/{idCommune}",
                        // "/api/commune/departement/{libelle}",
                        "/api/commune/departement/{idDepartement}",
                        "/api/commune/delete/{idCommune}",

                        // Arrondissement
                        "/api/arrondissement/create_arrondissement",
                        "/api/arrondissement/liste_arrondissements",
                        "/api/arrondissement/{idArrondissement}",
                        "/api/arrondissement/commune/{idCommune}",
                        "/api/arrondissement/delete/{idArrondissement}",

                        // quartiers
                        "/api/quartiers/create_quartier",
                        "/api/quartiers/liste_quartiers",
                        "/api/quartiers/rechercher/{idQuartier}",
                        "/api/quartiers/arrondissement/{idArrondissement}",
                        "/api/quartiers/delete/{idQuartier}",

                        // Interventions
                        "/api/interventions",
                        "/api/interventions/{id}",
                        "/enums/niveaux-priorite",
                        "/enums/statut-interventions",

                        "/api/offres/all",
                        "/api/offres/{id}",
                        "/api/offres/actives",
                        "/api/offres/**",

                        "/api/password-reset/**",
                        "/api/request-password-reset",
                        "/api/reset-password",
                        "/api/notifications/user",
                        "/api/notifications/**"

        };

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
                return httpSecurity.csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS avant tout
                                .addFilterBefore(oAuth2CorsBypassFilter, CorsFilter.class)
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(SWAGGER_WHITELIST)
                                                .permitAll()
                                                .requestMatchers("/reservation/confirm/**", "/reservation/cancel/**")
                                                .hasAnyAuthority("ADMIN_ROLES", "MANAGER_ROLES")
                                                .anyRequest().authenticated())

                                .authenticationProvider(authenticationProvider())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(customFirewallFilter, UsernamePasswordAuthenticationFilter.class)
                                .httpBasic(http -> http.disable())
                                .formLogin(form -> form.disable())
                                .oauth2Login(oauth -> oauth
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService)) // ✅ branché ici
                                                .successHandler(customOAuth2SuccessHandler)
                                                .failureUrl("/auth/oauth2/failure")
                                                .authorizationEndpoint(a -> a.baseUri("/auth/oauth2/authorize"))
                                                .redirectionEndpoint(r -> r.baseUri("/auth/oauth2/callback/*")))
                                .headers(headers -> headers
                                                .addHeaderWriter(new StaticHeadersWriter("Content-Security-Policy",
                                                                "default-src 'self'; " +
                                                                                "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdnjs.cloudflare.com https://translate.googleapis.com https://www.gstatic.com; "
                                                                                +
                                                                                "style-src 'self' 'unsafe-inline' https://cdnjs.cloudflare.com https://www.gstatic.com; "
                                                                                +
                                                                                "img-src 'self' data:; " +
                                                                                "connect-src 'self' http://localhost:8082 https://dev.mainsdorplus.com; "
                                                                                +
                                                                                "font-src 'self' https://cdnjs.cloudflare.com; "
                                                                                +
                                                                                "frame-src 'self'; " +
                                                                                "object-src 'none';"))
                                                .frameOptions(frameOptions -> frameOptions.deny()))
                                .build();
        }

        @SuppressWarnings("deprecation")
        @Bean
        AuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
                authenticationProvider.setUserDetailsService(userDetailsService);
                authenticationProvider.setPasswordEncoder(passwordEncoder);
                return authenticationProvider;
        }

        @Bean
        AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        public PermissionEvaluator permissionEvaluator() {
                return new CustomPermissionEvaluator();
        }

        @Bean
        UrlBasedCorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:8082",
                                "https://dev.mainsdorplus.com",
                                "https://www.dev.mainsdorplus.com"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type",
                                "X-Requested-With", "Accept", "Origin", "Accept-Encoding", "X-CSRF-Token"));
                configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        CorsFilter corsFilter() {
                return new CorsFilter(corsConfigurationSource());
        }
}
