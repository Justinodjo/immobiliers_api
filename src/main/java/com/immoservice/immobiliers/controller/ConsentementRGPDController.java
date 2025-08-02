package com.immoservice.immobiliers.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.immoservice.immobiliers.entities.*;
import com.immoservice.immobiliers.services.ConsentementRGPDService;
import com.immoservice.immobiliers.services.UserInfoService;

@RestController
@RequestMapping("/api/consentements")
public class ConsentementRGPDController {

    @Autowired
    private ConsentementRGPDService consentementRGPDService;

    @Autowired
    private UserInfoService userInfoService; // si vous avez un service pour récupérer l'utilisateur

    // Enregistrer un consentement
    @PostMapping("/enregistrer")
    public ConsentementRGPD enregistrerConsentement(
            @RequestParam Integer userId,
            @RequestParam TypeConsentement type,
            @RequestParam StatutConsentement statut,
            @RequestParam String ip,
            @RequestParam(required = false) String version) {

        UserInfo user = userInfoService.getUser(userId); // à implémenter si ce n’est pas déjà fait
        return consentementRGPDService.enregistrerConsentement(user, type, statut, ip, version);
    }

    // Lister tous les consentements d'un utilisateur
    @GetMapping("/utilisateur/{userId}")
    public List<ConsentementRGPD> getConsentementsParUtilisateur(@PathVariable Integer userId) {
        UserInfo user = userInfoService.getUser(userId);
        return consentementRGPDService.obtenirConsentementsParUser(user);
    }
}
