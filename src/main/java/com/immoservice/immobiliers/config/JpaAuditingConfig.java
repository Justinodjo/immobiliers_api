package com.immoservice.immobiliers.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
// import org.springframework.stereotype.Component;

// @Component
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {
    @Bean
    AuditorAware<String> auditorProvider() {

        return () -> Optional.of("utilisateur courant");

    }
}