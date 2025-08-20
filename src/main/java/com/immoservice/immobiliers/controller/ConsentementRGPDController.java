package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.entities.ConsentementRGPD;
import com.immoservice.immobiliers.services.ConsentementRGPDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/consentement")
public class ConsentementRGPDController {

    @Autowired
    private ConsentementRGPDService consentementService;

    @PostMapping("/add")
    public ConsentementRGPD addConsentement(@RequestBody ConsentementRGPD consentement) {
        
        return consentementService.saveConsentement(consentement);
    }

    @GetMapping("/all")
    public List<ConsentementRGPD> getAllConsentements() {
        return consentementService.getAllConsentements();
    }

    @GetMapping("/get/{id}")
    public Optional<ConsentementRGPD> getConsentementById(@PathVariable Long id) {
        return consentementService.getConsentementById(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteConsentement(@PathVariable Long id) {
        consentementService.deleteConsentement(id);
    }

    @GetMapping("/by-user/{userId}")
    public List<ConsentementRGPD> getByUserId(@PathVariable Long userId) {
        return consentementService.getConsentementsByUserId(userId);
    }
}
