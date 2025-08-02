package com.immoservice.immobiliers.services;

import com.immoservice.immobiliers.config.TwilioProperties;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private final String FROM_PHONE = "+14155238886"; // Numéro Twilio
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
        Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(FROM_PHONE),
                message).create();
    }
}
