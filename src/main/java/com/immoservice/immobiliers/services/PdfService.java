package com.immoservice.immobiliers.services;

import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.services.pdf.PdfContractGenerator;
import com.immoservice.immobiliers.services.pdf.PdfGeneratorFactory;

@Service
public class PdfService {

    private final PdfGeneratorFactory factory;

    public PdfService(PdfGeneratorFactory factory) {
        this.factory = factory;
    }

    public byte[] generate(String typeContrat, Object data) throws Exception {
        PdfContractGenerator generator = factory.getGenerator(typeContrat);
        return generator.generate(data);
    }
}
