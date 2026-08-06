package com.immoservice.immobiliers.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.immoservice.immobiliers.entities.Visite360;
import com.immoservice.immobiliers.repository.Visite360Repository;

@Service
public class Visite360Service {

    @Autowired
    private Visite360Repository repository;

    public Visite360 createVisite(Visite360 dto) {
        Visite360 visite = new Visite360();
        visite.setUrlEmbed(dto.getUrlEmbed());
        visite.setPlateforme(detectPlateforme(dto.getUrlEmbed()));
        visite.setNombreVues(0);
        visite.setDateCreation(new Date());
        return repository.save(visite);
    }

    public List<Visite360> findByBienId(Long bienId) {
        return repository.findByBienId(bienId);
    }
    public void incrementVue(Long id) {
        Visite360 visite = repository.findById(id).orElseThrow();
        visite.setNombreVues(visite.getNombreVues() + 1);
        repository.save(visite);
    }

    private String detectPlateforme(String url) {
        if (url.contains("matterport"))
            return "matterport";
        if (url.contains("kuula"))
            return "kuula";
        if (url.contains("cloudpano"))
            return "cloudpano";
        return "autre";
    }
}