package com.immoservice.immobiliers.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
        // Navigate to http://localhost:8080/swagger-ui/index.html – this is the VIP
        // entrance
        // to your API documentation.
        @Bean
        GroupedOpenApi publicApi() {
                return GroupedOpenApi.builder()
                                .group("public-apis")
                                .pathsToMatch("/**")
                                .build();
        }

        @Bean
        OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info().title("Projet IMMOSERVICES").version("API 0.0.1"))
                                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                                .components(
                                                new Components()
                                                                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")));
        }
}
