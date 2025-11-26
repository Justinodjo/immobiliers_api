package com.immoservice.immobiliers.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.immoservice.immobiliers.dto.PasswordResetRequestDTO;
import com.immoservice.immobiliers.dto.ResetPasswordDTO;
import com.immoservice.immobiliers.dto.VerifyCodeDTO;
import com.immoservice.immobiliers.exceptions.InvalidTokenException;
import com.immoservice.immobiliers.exceptions.TokenExpiredException;
import com.immoservice.immobiliers.services.PasswordResetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/password-reset")
public class PasswordResetController {
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);
    @Autowired
    private PasswordResetService passwordResetService;

    /**
     * Étape 1: Demander une réinitialisation de mot de passe
     * Envoie un email + SMS + notification avec le code
     */
    @PostMapping("/request")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody PasswordResetRequestDTO request) {
        try {
            String token = passwordResetService.initiatePasswordReset(request.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", token);
            response.put("message",
                    "Un code de vérification a été envoyé par email, SMS et notification. Vérifiez vos messages.");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Aucun compte n'est associé à cet email.");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Une erreur est survenue. Veuillez réessayer.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Étape 2: Vérifier le code de vérification
     */
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody VerifyCodeDTO verifyCodeDTO) {
        logger.info("DTO reçu: token={}, code={}", verifyCodeDTO.getToken(), verifyCodeDTO.getCode());
        try {
            boolean isValid = passwordResetService.verifyCode(
                    verifyCodeDTO.getToken(),
                    verifyCodeDTO.getCode());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("verified", isValid);
            response.put("message",
                    "Code vérifié avec succès. Vous pouvez maintenant définir votre nouveau mot de passe.");

            return ResponseEntity.ok(response);

        } catch (InvalidTokenException | TokenExpiredException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Une erreur est survenue lors de la vérification.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Étape 3: Réinitialiser le mot de passe
     */
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        try {
            // Vérifier que les mots de passe correspondent
            if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmPassword())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Les mots de passe ne correspondent pas");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            passwordResetService.resetPassword(
                    resetPasswordDTO.getToken(),
                    resetPasswordDTO.getNewPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message",
                    "Votre mot de passe a été réinitialisé avec succès. Vous pouvez maintenant vous connecter.");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | InvalidTokenException | TokenExpiredException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Une erreur est survenue lors de la réinitialisation.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Renvoyer un nouveau code
     */
    @PostMapping("/resend")
    public ResponseEntity<?> resendCode(@Valid @RequestBody PasswordResetRequestDTO request) {
        try {
            passwordResetService.resendCode(request.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Un nouveau code a été envoyé.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors du renvoi du code.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
