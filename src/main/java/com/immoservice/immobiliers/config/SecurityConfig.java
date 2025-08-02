package com.immoservice.immobiliers.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                        "/v3/api-docs/**",
                        "/v3/api-docs/public-apis",
                        "/auth/welcome",
                        "/auth/addUser",
                        "/api/reset-password",
                        "/api/request-password-reset",
                        "/auth/login",
                        "/auth/logout",
                        "/contact/send",
                        "/tfe/uploads/**",
                        "/swagger-ui/**",
                        "/swagger-ui/index.html",
                        "/public-apis/**",
                        "/auth/getUsers/{id}", "/auth/getUsers",
                        "/swagger-resources/**",
                        "/swagger-resources"
        };

        @Bean
        public UserDetailsService userDetailsService() {
                return new UserInfoService();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
                return httpSecurity.csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS avant tout
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(SWAGGER_WHITELIST)
                                                .permitAll()
                                                .requestMatchers("/fichier/download/{id}",
                                                                "/fichier//download/{nom}", "/auth/getUsers/{id}",
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
                                                                "/biens/get/{id}",
                                                                "/typeBiens/get/{id}",
                                                                "/typeBiens/all",
                                                                "/details_biens/all",
                                                                "/details_biens/get/{id}")
                                                .permitAll()
                                                .requestMatchers("/reservation/confirm/**", "/reservation/cancel/**")
                                                .hasAnyRole("ADMIN_ROLES", "MANAGER_ROLES")
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider())
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(customFirewallFilter, UsernamePasswordAuthenticationFilter.class)
                                .headers(headers -> headers
                                                .contentSecurityPolicy(csp -> csp
                                                                .policyDirectives(
                                                                                "default-src 'self'; script-src 'self'; object-src 'none'; base-uri 'self'; frame-ancestors 'none';"))
                                                .frameOptions(frameOptions -> frameOptions.deny()))
                                .build();
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
                authenticationProvider.setUserDetailsService(userDetailsService());
                authenticationProvider.setPasswordEncoder(passwordEncoder());
                return authenticationProvider;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        public UrlBasedCorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                // configuration.setAllowedOrigins(Arrays.asList("https://www.mainsdorplus.com","http://localhost:5173",
                // "https://mainsdorplus.com"));
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type",
                                "X-Requested-With", "Accept", "Origin", "Accept-Encoding", "X-CSRF-Token"));
                configuration.setExposedHeaders(Arrays.asList(
                                "Authorization",
                                "Content-Disposition"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        public CorsFilter corsFilter() {
                return new CorsFilter(corsConfigurationSource());
        }
}
