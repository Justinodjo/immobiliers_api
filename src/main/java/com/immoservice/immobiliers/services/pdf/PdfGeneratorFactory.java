package com.immoservice.immobiliers.services.pdf;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class PdfGeneratorFactory {

    private final Map<String, PdfContractGenerator<?>> generators;

    public PdfGeneratorFactory(Map<String, PdfContractGenerator<?>> generators) {
        this.generators = generators;
    }

    public PdfContractGenerator<?> getGenerator(String type) {
        PdfContractGenerator<?> generator = generators.get(type.toUpperCase());
        if (generator == null) {
            throw new IllegalArgumentException("Type de contrat inconnu : " + type);
        }
        return generator;
    }
}
