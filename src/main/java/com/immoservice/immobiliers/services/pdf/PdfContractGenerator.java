package com.immoservice.immobiliers.services.pdf;

public interface PdfContractGenerator<T> {
    byte[] generate(T data) throws Exception;
}
