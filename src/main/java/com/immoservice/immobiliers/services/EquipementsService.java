package com.immoservice.immobiliers.services;

import com.immoservice.immobiliers.entities.Equipements;
import com.immoservice.immobiliers.repository.EquipementsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipementsService {

    private final EquipementsRepository equipementsRepository;

    public EquipementsService(EquipementsRepository equipementsRepository) {
        this.equipementsRepository = equipementsRepository;
    }

    public Equipements save(Equipements equipements) {
        return equipementsRepository.save(equipements);
    }

    public Optional<Equipements> findById(Long id) {
        return equipementsRepository.findById(id);
    }

    public List<Equipements> findAll() {
        return equipementsRepository.findAll();
    }

    public Equipements update(Long id, Equipements newEquipements) {
        return equipementsRepository.findById(id).map(existing -> {
            existing.setActivites(newEquipements.getActivites());
            existing.setModesPaiement(newEquipements.getModesPaiement());
            existing.setServices(newEquipements.getServices());
            existing.setSecurite(newEquipements.getSecurite());
            existing.setLanguesPersonnel(newEquipements.getLanguesPersonnel());
            return equipementsRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Équipement non trouvé avec l'id " + id));
    }

    public void delete(Long id) {
        equipementsRepository.deleteById(id);
    }
}
