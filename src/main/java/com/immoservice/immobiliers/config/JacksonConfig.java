package com.immoservice.immobiliers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class JacksonConfig {

@Bean
public ObjectMapper objectMapper() {
ObjectMapper objectMapper = new ObjectMapper();

// Activer l'indentation pour une sortie plus lisible
objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

// Ajoute d'autres configurations si nécessaire

        // ⚡ Important : support des types Java 8 Time (LocalDate, LocalDateTime, etc.)
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // ⚡ Ignorer les champs inconnus globalement
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
return objectMapper;
}
}
