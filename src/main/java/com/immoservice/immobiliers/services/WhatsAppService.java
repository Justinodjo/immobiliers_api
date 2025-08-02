package com.immoservice.immobiliers.services;

import com.immoservice.immobiliers.config.TwilioProperties;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {
    private final String FROM_WHATSAPP = "whatsapp:+14155238886"; // Twilio Sandbox
    @Autowired
    private final TwilioProperties twilioProperties;

    public WhatsAppService(TwilioProperties twilioProperties) {
        this.twilioProperties = twilioProperties;
    }

    @PostConstruct
    public void init() {
        Twilio.init(twilioProperties.getSid(), twilioProperties.getToken());
    }

    public void sendWhatsApp(String to, String message) {
        Message.creator(
                new PhoneNumber("whatsapp:" + to),
                new PhoneNumber(FROM_WHATSAPP),
                message).create();
    }
}
