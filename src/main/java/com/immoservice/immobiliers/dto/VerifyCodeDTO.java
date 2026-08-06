package com.immoservice.immobiliers.dto;

import jakarta.validation.constraints.NotBlank;

public class VerifyCodeDTO {
    @NotBlank(message = "Le token est obligatoire")
    private String token;

    @NotBlank(message = "Le code est obligatoire")
    private String code;

    // GETTERS & SETTERS
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
