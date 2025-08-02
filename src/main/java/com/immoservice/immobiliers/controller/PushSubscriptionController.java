package com.immoservice.immobiliers.controller;

import org.springframework.web.bind.annotation.*;

import com.immoservice.immobiliers.entities.Subscription;

@RestController
@RequestMapping("/api/push")
public class PushSubscriptionController {

    // Injectez un service ou repo pour stocker les abonnements

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody Subscription subscription) {
        // Sauvegardez en base ou en mémoire l'abonnement reçu
    }

}
