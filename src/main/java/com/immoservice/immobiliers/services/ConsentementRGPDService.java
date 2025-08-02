package com.immoservice.immobiliers.services;

import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.entities.*;
import com.immoservice.immobiliers.repository.ConsentementRGPDRepository;

@Service
public class ConsentementRGPDService {

    private final ConsentementRGPDRepository repository;

    public ConsentementRGPDService(ConsentementRGPDRepository repository) {
        this.repository = repository;
    }

    public ConsentementRGPD enregistrerConsentement(UserInfo user, TypeConsentement type, StatutConsentement statut,
            String ip, String version) {
        ConsentementRGPD consentement = new ConsentementRGPD();
        consentement.setUser(user);
        consentement.setTypeConsentement(type);
        consentement.setStatut(statut);
        consentement.setDateConsentement(new Date());
        consentement.setIpAddress(ip);
        consentement.setVersionPolitique(version);
        return repository.save(consentement);
    }

    public List<ConsentementRGPD> obtenirConsentementsParUser(UserInfo user) {
        return repository.findByUser(user);
    }
}
