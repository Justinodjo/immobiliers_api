package com.immoservice.immobiliers.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.config.SmsSendException;
import com.immoservice.immobiliers.config.TwilioProperties;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;

@Service
public class SmsService {
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    // private final String FROM_PHONE = "+14155238886"; // Numéro Twilio
    private final String FROM_PHONE = "+2290161366896"; // Numéro Twilio
    @Autowired
    private final TwilioProperties twilioProperties;

    public SmsService(TwilioProperties twilioProperties) {
        this.twilioProperties = twilioProperties;
    }

    @PostConstruct
    public void init() {
        Twilio.init(twilioProperties.getSid(), twilioProperties.getToken());
    }

    public void sendSms(String to, String message) {
        try {
            Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(FROM_PHONE),
                    message).create();
        } catch (Exception e) {
            // Log l'erreur et lance une exception ou retourne une erreur personnalisée
            logger.error("Erreur lors de l'envoi du SMS à {}: {}", to, e.getMessage());
            throw new SmsSendException("Impossible d'envoyer le SMS. Veuillez réessayer plus tard.");
        }
    }
}
