package com.immoservice.immobiliers.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.immoservice.immobiliers.entities.Visite360;
import com.immoservice.immobiliers.services.Visite360Service;

@RestController
@RequestMapping("/api/visite360")
public class Visite360Controller {

    @Autowired
    private Visite360Service service;

    @PostMapping("/add")
    public ResponseEntity<Visite360> addVisite(@RequestBody Visite360 dto) {
        return ResponseEntity.ok(service.createVisite(dto));
    }

    @GetMapping("/bien/{bienId}")
    public ResponseEntity<List<Visite360>> getVisitesByBien(@PathVariable Long bienId) {
        return ResponseEntity.ok(service.findByBienId(bienId));
    }

    @PostMapping("/vue/{id}")
    public ResponseEntity<Void> incrementVue(@PathVariable Long id) {
        service.incrementVue(id);
        return ResponseEntity.ok().build();
    }
}
