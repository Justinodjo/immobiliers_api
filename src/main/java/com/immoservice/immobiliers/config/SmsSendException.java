package com.immoservice.immobiliers.config;

public class SmsSendException extends RuntimeException {
    public SmsSendException(String message) {
        super(message);
    }
}
