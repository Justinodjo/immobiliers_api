package com.immoservice.immobiliers.services;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.concurrent.ExecutionException;

@Service
public class WebPushService {

    static {
        // Ajouter BouncyCastle comme provider si absent
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private final PushService pushService;

    // Injectez vos clés VAPID et votre contact mail ici (ex:
    // mailto:admin@votreapp.com)
    private static final String PUBLIC_KEY = "BLXQ_L6U0Ld14Z3SZe7OF-AXjXHkVDuPKN3hojFz_ejOyUxIvsw58jbs00i0C_UTC1YY_6A9Zg_yqyt5Ke5pEjQ";
    private static final String PRIVATE_KEY = "ZdsOhD5w-j9f8QT8Qc0iJ_C7xImDGCasWJrcn0mj8LE";
    private static final String SUBJECT = "mailto:justinodjo07@gmail.com";

    public WebPushService() {
        try {
            this.pushService = new PushService();
            pushService.setPublicKey(PUBLIC_KEY);
            pushService.setPrivateKey(PRIVATE_KEY);
            pushService.setSubject(SUBJECT);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur d'initialisation de PushService", e);
        }
    }

    public void sendNotification(Subscription subscription, String payload)
            throws GeneralSecurityException, IOException, JoseException, ExecutionException, InterruptedException {
        Notification notification = new Notification(subscription, payload);
        pushService.send(notification);
    }
}
