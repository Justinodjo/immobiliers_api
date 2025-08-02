package com.immoservice.immobiliers.services;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.immoservice.immobiliers.entities.Alertes;
import com.immoservice.immobiliers.entities.BiensImmobiliers;
// import com.immoservice.immobiliers.repository.FichierRepository;
import com.immoservice.immobiliers.repository.AlertesRepository;

@Service
public class AlertesService{
     @Autowired
    private AlertesRepository alertesRepository;

    public void envoyerAlertes(BiensImmobiliers biensImmobiliers, String message) {
        Alertes alerte = new Alertes();

        alerte.setBiensImmobilers(biensImmobiliers);
        alerte.setMessage(message);
        alerte.setDateAlertes(new Date());

        alertesRepository.save(alerte);
    }
}