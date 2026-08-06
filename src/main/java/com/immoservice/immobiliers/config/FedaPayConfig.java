package com.immoservice.immobiliers.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.fedapay.model.FedaPay;

import jakarta.annotation.PostConstruct;

@Configuration
public class FedaPayConfig {

    @Autowired
    private Environment environment; // <-- injection de l'environnement Spring

    @PostConstruct
    public void init() {
        String apiKey = environment.getProperty("fedapay.api.key");
        FedaPay.apiKey = apiKey;
    }
}
