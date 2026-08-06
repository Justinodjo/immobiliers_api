package com.immoservice.immobiliers.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.immoservice.immobiliers.entities.PasswordResetToken;
import com.immoservice.immobiliers.entities.TypeNotifications;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.exceptions.InvalidTokenException;
import com.immoservice.immobiliers.exceptions.TokenExpiredException;
import com.immoservice.immobiliers.repository.PasswordResetTokenRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;

@Service
public class PasswordResetService {
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);
    private static final int CODE_LENGTH = 6;
    private static final int TOKEN_VALIDITY_MINUTES = 15;
    private static final int MAX_ATTEMPTS = 3;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TwilioVerifyService twilioVerifyService;

    /**
     * Génère un code de vérification à 6 chiffres
     */
    private String generateVerificationCode() {
        Random random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * Génère un token sécurisé
     */
    private String generateSecureToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Initie la réinitialisation du mot de passe
     */
    @Transactional
    public String initiatePasswordReset(String email) {
        logger.info("Demande de réinitialisation de mot de passe pour: {}", email);
        try {
            // 1. Vérifier que l'utilisateur existe
            UserInfo user = userInfoRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Aucun utilisateur trouvé avec cet email"));

            // 2. Supprimer les anciens tokens non utilisés
            passwordResetTokenRepository.deleteByUserInfo(user);

            // 3. Générer un nouveau code de vérification et un token
            String verificationCode = generateVerificationCode();
            String token = generateSecureToken();

            // 4. Calculer la date d'expiration (15 minutes)
            Date expiryDate = new Date(System.currentTimeMillis() + TOKEN_VALIDITY_MINUTES * 60 * 1000);

            // 5. Créer et sauvegarder le token
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setVerificationCode(verificationCode);
            resetToken.setUserInfo(user);
            resetToken.setExpiryDate(expiryDate);
            resetToken.setAttempts(0);
            resetToken.setUsed(false);
            passwordResetTokenRepository.save(resetToken);

            // 7. Envoyer SMS via Twilio (ne pas bloquer si ça échoue)
            try {
                sendPasswordResetSMSTwilio(user, verificationCode);
                logger.info("📱 SMS Twilio envoyé avec succès");
            } catch (Exception e) {
                logger.error("❌ Erreur envoi SMS Twilio (continuer quand même): {}",
                        e.getMessage());
            }

            // 8. Créer une notification dans l'application
            try {
                createPasswordResetNotification(user, verificationCode);
                logger.info("🔔 Notification créée avec succès");
            } catch (Exception e) {
                logger.error("❌ Erreur création notification (continuer quand même): {}", e.getMessage());
            }
            // 6. Envoyer l'email avec le code (ne pas bloquer si ça échoue)
            try {
                sendPasswordResetEmail(user, verificationCode, token);
                logger.info("Code de vérification envoyé avec succès à {}", email);
            } catch (Exception e) {
                logger.error("❌ Erreur envoi email (continuer quand même): {}", e.getMessage());
            }
            // OU
            twilioVerifyService.sendVerification(user.getNumero()); // Via Twilio

            // sendPasswordResetEmail(user, verificationCode, token);

            return token;
        } catch (IllegalArgumentException e) {
            logger.error("❌ Utilisateur non trouvé: {}", email);
            throw e;
        } catch (Exception e) {
            logger.error("❌ Erreur inattendue lors de l'initiation: ", e);
            throw new RuntimeException("Erreur lors de l'initiation de la réinitialisation", e);
        }
    }

    /**
     * Envoie l'email de réinitialisation
     */
    private void sendPasswordResetEmail(UserInfo user, String code, String token) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", user.getName() + " " + user.getSecondName());
            variables.put("verificationCode", code);
            variables.put("resetLink", "https://dev.mainsdorplus.com/password-reset/verify?token=" + token);
            variables.put("expirationMinutes", TOKEN_VALIDITY_MINUTES);

            emailService.envoyerEmail(
                    user.getEmail(),
                    "Réinitialisation de votre mot de passe",
                    "email/password-reset-email", // Template Thymeleaf
                    variables);

            logger.info("📧 Email de réinitialisation envoyé à {}", user.getEmail());
        } catch (Exception e) {
            logger.error("❌ Erreur lors de l'envoi de l'email à {}: {}", user.getEmail(), e.getMessage(), e);
            throw e; // Remonter l'erreur pour voir exactement ce qui bloque
        }
    }

    /**
     * Envoie le SMS via Twilio
     */
    private void sendPasswordResetSMSTwilio(UserInfo user, String code) {
        try {
            if (user.getNumero() != null && !user.getNumero().isEmpty()) {
                // Option 1 : Twilio Verify (recommandé)
                twilioVerifyService.sendVerification(user.getNumero());
                logger.info("📱 OTP Twilio Verify envoyé à {}", user.getNumero());

                // Option 2 : SMS personnalisé via ton service custom
                // String message = String.format(
                // "MainsDorPlus: Votre code est %s. Valide %d min.",
                // code, TOKEN_VALIDITY_MINUTES);
                // smsService.sendSms(user.getNumero(), message);
            } else {
                logger.warn("⚠️ Numéro de téléphone manquant pour l'utilisateur {}", user.getEmail());
            }
        } catch (Exception e) {
            logger.error("❌ Erreur lors de l'envoi du SMS à {}: {}", user.getNumero(), e.getMessage(), e);
            // Ne pas bloquer le processus si le SMS échoue
        }
    }

    /**
     * Crée une notification dans l'application
     */
    private void createPasswordResetNotification(UserInfo user, String code) {
        try {
            String message = String.format(
                    "Code de vérification: %s\nValide pendant %d minutes.\nNe partagez ce code avec personne.",
                    code,
                    TOKEN_VALIDITY_MINUTES);

            notificationService.creerNotificationComplete(
                    user.getId(),
                    "Réinitialisation de mot de passe",
                    message,
                    TypeNotifications.ALERTE,
                    false,
                    null,
                    "HIGH");

            logger.info("Notification créée pour l'utilisateur {}", user.getId());
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la notification: {}", e.getMessage());
        }
    }

    /**
     * Vérifie le code de vérification
     */
    @Transactional
    public boolean verifyCode(String token, String code) {
        logger.info("Vérification du code pour le token: {}", token);

        // 1. Récupérer le token
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token invalide"));

        // 2. Vérifier si le token est expiré
        if (resetToken.getExpiryDate().before(new Date())) {
            logger.warn("Token expiré pour l'utilisateur {}", resetToken.getUserInfo().getEmail());
            throw new TokenExpiredException("Le code de vérification a expiré. Veuillez redemander un nouveau code.");
        }

        // 3. Vérifier si le token a déjà été utilisé
        if (resetToken.isUsed()) {
            logger.warn("Tentative d'utilisation d'un token déjà utilisé");
            throw new InvalidTokenException("Ce code a déjà été utilisé");
        }

        // 4. Vérifier le nombre de tentatives
        if (resetToken.getAttempts() >= MAX_ATTEMPTS) {
            logger.warn("Nombre maximum de tentatives atteint pour le token");
            passwordResetTokenRepository.delete(resetToken);
            throw new InvalidTokenException(
                    "Nombre maximum de tentatives atteint. Veuillez redemander un nouveau code.");
        }

        // 5. Vérifier le code
        if (!resetToken.getVerificationCode().equals(code)) {
            resetToken.setAttempts(resetToken.getAttempts() + 1);
            passwordResetTokenRepository.save(resetToken);

            int remainingAttempts = MAX_ATTEMPTS - resetToken.getAttempts();
            logger.warn("Code incorrect. Tentatives restantes: {}", remainingAttempts);

            throw new InvalidTokenException(
                    String.format("Code incorrect. Il vous reste %d tentative(s)", remainingAttempts));
        }

        // 6. Code valide - marquer le token comme vérifié
        resetToken.setCodeVerified(true);
        passwordResetTokenRepository.save(resetToken);

        logger.info("Code vérifié avec succès pour l'utilisateur {}", resetToken.getUserInfo().getEmail());
        return true;
    }

    /**
     * Réinitialise le mot de passe
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        logger.info("Réinitialisation du mot de passe pour le token: {}", token);

        // 1. Récupérer le token
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token invalide"));

        // 2. Vérifier que le code a été vérifié
        if (!resetToken.isCodeVerified()) {
            throw new InvalidTokenException("Veuillez d'abord vérifier le code de sécurité");
        }

        // 3. Vérifier si le token est expiré
        if (resetToken.getExpiryDate().before(new Date())) {
            throw new TokenExpiredException("Le délai de réinitialisation a expiré");
        }

        // 4. Vérifier si le token a déjà été utilisé
        if (resetToken.isUsed()) {
            throw new InvalidTokenException("Ce code a déjà été utilisé");
        }

        // 5. Récupérer l'utilisateur
        UserInfo user = resetToken.getUserInfo();

        // 6. Vérifier que le nouveau mot de passe est différent de l'ancien
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit être différent de l'ancien");
        }

        // 7. Mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        userInfoRepository.save(user);

        // 8. Marquer le token comme utilisé
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        // 9. Envoyer une confirmation par email
        sendPasswordChangedConfirmation(user);

        // 10. Créer une notification de sécurité
        createPasswordChangedNotification(user);

        // 11. Envoyer un SMS de confirmation
        sendPasswordChangedSMS(user);

        logger.info("Mot de passe réinitialisé avec succès pour {}", user.getEmail());
    }

    /**
     * Envoie un email de confirmation de changement de mot de passe
     */
    private void sendPasswordChangedConfirmation(UserInfo user) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", user.getName() + " " + user.getSecondName());
            variables.put("changeDate", LocalDateTime.now());
            variables.put("supportEmail", "support@mainsdorplus.com");

            emailService.envoyerEmail(
                    user.getEmail(),
                    "Votre mot de passe a été modifié",
                    "password-changed-confirmation",
                    variables);

            logger.info("Email de confirmation envoyé à {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de confirmation: {}", e.getMessage());
        }
    }

    /**
     * Envoie un SMS de confirmation
     */
    private void sendPasswordChangedSMS(UserInfo user) {
        try {
            if (user.getNumero() != null && !user.getNumero().isEmpty()) {
                String message = "MainsDorPlus: Votre mot de passe a été modifié avec succès. Si ce n'était pas vous, contactez-nous immédiatement.";
                smsService.sendSms(user.getNumero(), message);
                logger.info("SMS de confirmation envoyé à {}", user.getNumero());
            }
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi du SMS de confirmation: {}", e.getMessage());
        }
    }

    /**
     * Crée une notification de sécurité
     */
    private void createPasswordChangedNotification(UserInfo user) {
        try {
            String message = "Votre mot de passe a été modifié avec succès. " +
                    "Si vous n'êtes pas à l'origine de cette action, contactez immédiatement notre support.";

            notificationService.creerNotificationComplete(
                    user.getId(),
                    "Mot de passe modifié",
                    message,
                    TypeNotifications.ALERTE,
                    false,
                    null,
                    "HIGH");

            logger.info("Notification de changement de mot de passe créée pour {}", user.getId());
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la notification: {}", e.getMessage());
        }
    }

    /**
     * Renvoie un nouveau code
     */
    @Transactional
    public void resendCode(String email) {
        logger.info("Renvoi du code pour: {}", email);
        initiatePasswordReset(email);
    }

    /**
     * Nettoie les tokens expirés (à exécuter périodiquement)
     */
    @Transactional
    public void cleanExpiredTokens() {
        Date now = new Date();
        List<PasswordResetToken> expiredTokens = passwordResetTokenRepository.findByExpiryDateBefore(now);
        passwordResetTokenRepository.deleteAll(expiredTokens);
        logger.info("Nettoyage: {} tokens expirés supprimés", expiredTokens.size());
    }

    private void sendPasswordResetSMS(UserInfo user) {
        try {
            if (user.getNumero() != null && !user.getNumero().isEmpty()) {
                twilioVerifyService.sendVerification(user.getNumero());
                logger.info("OTP envoyé via Twilio à {}", user.getNumero());
            }
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi du SMS via Twilio à {}: {}", user.getNumero(), e.getMessage());
        }
    }

}
