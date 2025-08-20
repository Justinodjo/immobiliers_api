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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.immoservice.immobiliers.filter.JwtFilter;
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

        // @Autowired
        // private UserDetailsService userDetailsService;

        private final String[] SWAGGER_WHITELIST = {
                        "/v3/api-docs/**", // OpenAPI JSON
                        "/swagger-ui/**", // Swagger UI assets
                        "/swagger-ui.html", // Swagger main HTML
                        "/swagger-resources/**", // Swagger resources
                        "/swagger-resources",
                        "/public-apis/**",
                        "/webjars/**",

                        // Auth endpoints
                        "/auth/welcome",
                        "/auth/addUser",
                        "/auth/login",
                        "/auth/logout",
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
                        "/api/roles/liste",
                        "/fichier/add",
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
                        "/typeBiens/get/{id}",
                        "/typeBiens/all",
                        "/details_biens/all",
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
                        "/api/avis/bien/{id}"
        };

        @Bean
        UserDetailsService userDetailsService() {
                return new UserInfoService();
        }

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
                return httpSecurity.csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS avant tout
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(SWAGGER_WHITELIST)
                                                .permitAll()
                                                .requestMatchers("/reservation/confirm/**", "/reservation/cancel/**")
                                                .hasAnyAuthority("ADMIN_ROLES", "MANAGER_ROLES")
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider())
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class)
                                // .addFilterBefore(rateLimitingFilter,
                                // UsernamePasswordAuthenticationFilter.class)
                                // .addFilterBefore(customFirewallFilter,
                                // UsernamePasswordAuthenticationFilter.class)
                                .headers(headers -> headers
                                                .addHeaderWriter(new StaticHeadersWriter("Content-Security-Policy",
                                                                "default-src 'self'; " +
                                                                                "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdnjs.cloudflare.com https://translate.googleapis.com https://www.gstatic.com; "
                                                                                +
                                                                                "style-src 'self' 'unsafe-inline' https://cdnjs.cloudflare.com https://www.gstatic.com; "
                                                                                +
                                                                                "img-src 'self' data:; " +
                                                                                "connect-src 'self' http://localhost:8080 https://api.yourbackend.com; "
                                                                                +
                                                                                "font-src 'self' https://cdnjs.cloudflare.com; "
                                                                                +
                                                                                "frame-src 'self'; " +
                                                                                "object-src 'none';"))
                                                .frameOptions(frameOptions -> frameOptions.deny()))
                                .build();
        }

        @Bean
        AuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
                authenticationProvider.setUserDetailsService(userDetailsService());
                authenticationProvider.setPasswordEncoder(passwordEncoder());
                return authenticationProvider;
        }

        @Bean
        PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
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
                // configuration.setAllowedOrigins(Arrays.asList("https://www.mainsdorplus.com","http://localhost:5173",
                // "https://mainsdorplus.com"));
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
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
