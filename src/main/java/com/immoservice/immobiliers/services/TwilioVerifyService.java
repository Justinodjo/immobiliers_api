package com.immoservice.immobiliers.services;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.entities.SmsCounter;
import com.immoservice.immobiliers.repository.SmsCounterRepository;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;

@Service
public class TwilioVerifyService {
    private static final Logger logger = LoggerFactory.getLogger(TwilioVerifyService.class);
    @Autowired
    private SmsCounterRepository smsCounterRepository;

    private static final int MAX_SMS_PER_DAY = 3;
    @Value("${twilio.sid}")
    private String twilioSid;

    @Value("${twilio.token}")
    private String twilioToken;

    @Value("${twilio.verify.sid}")
    private String verifySid;

    public TwilioVerifyService(
            @Value("${twilio.sid}") String sid,
            @Value("${twilio.token}") String token) {
        Twilio.init(sid, token);
    }

    // Envoyer OTP
    public void sendVerification(String phoneNumber) {
        // ✅ Valider le numéro AVANT d'appeler Twilio
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            logger.warn("⚠️ Numéro de téléphone vide - SMS non envoyé");
            return; // Ne pas lancer d'exception, juste ignorer
        }

        // ✅ S'assurer que le numéro est au format international
        // Formater le numéro
        String formattedNumber = phoneNumber.startsWith("+") ? phoneNumber : "+229" + phoneNumber;
        // Vérifier le compteur SMS
        LocalDate today = LocalDate.now();
        SmsCounter counter = smsCounterRepository.findByPhoneNumberAndDay(formattedNumber, today)
                .orElseGet(() -> {
                    SmsCounter newCounter = new SmsCounter();
                    newCounter.setPhoneNumber(formattedNumber);
                    newCounter.setDay(today);
                    newCounter.setCount(0);
                    return newCounter;
                });

        if (counter.getCount() >= MAX_SMS_PER_DAY) {
            logger.warn("⚠️ Limite quotidienne atteinte pour {} - SMS non envoyé", formattedNumber);
            return;
        }
        try {
            Verification verification = Verification.creator(
                    verifySid,
                    formattedNumber,
                    "sms")
                    .create();

            logger.info("✅ OTP Twilio envoyé à {} - SID: {}", formattedNumber, verification.getSid());
            // Incrémenter le compteur
            counter.setCount(counter.getCount() + 1);
            smsCounterRepository.save(counter);
        } catch (Exception e) {
            logger.error("❌ Erreur Twilio pour {}: {}", formattedNumber, e.getMessage());
            // Ne pas bloquer le processus
        }
    }

    // Vérifier OTP
    public boolean checkVerification(String phoneNumber, String code) {
        try {
            // ✅ Formater le numéro de la même manière
            String formattedNumber = phoneNumber;
            if (!phoneNumber.startsWith("+")) {
                formattedNumber = "+229" + phoneNumber;
            }

            VerificationCheck verificationCheck = VerificationCheck.creator(
                    verifySid,
                    code)
                    .setTo(formattedNumber)
                    .create();

            return "approved".equalsIgnoreCase(verificationCheck.getStatus());
        } catch (Exception e) {
            logger.error("❌ Erreur vérification Twilio: {}", e.getMessage());
            return false;
        }
    }
}