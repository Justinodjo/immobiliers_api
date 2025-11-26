package com.immoservice.immobiliers.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Convertit le chemin local en URI
        String resourceLocation = Paths.get(uploadPath).toUri().toString();

        registry.addResourceHandler("/tfe/uploads/**")
                .addResourceLocations(resourceLocation)
                .setCachePeriod(3600); // Cache pour 1h (facultatif)
    }
}
