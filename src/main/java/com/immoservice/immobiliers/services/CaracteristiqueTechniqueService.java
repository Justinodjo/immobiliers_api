package com.immoservice.immobiliers.services;

import com.immoservice.immobiliers.entities.CaracteristiqueTechnique;
import com.immoservice.immobiliers.repository.CaracteristiqueTechniqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CaracteristiqueTechniqueService {

    @Autowired
    private CaracteristiqueTechniqueRepository caracteristiqueTechniqueRepository;

    public List<CaracteristiqueTechnique> findAll() {
        return caracteristiqueTechniqueRepository.findAll();
    }

    public Optional<CaracteristiqueTechnique> findById(Long id) {
        return caracteristiqueTechniqueRepository.findById(id);
    }

    public CaracteristiqueTechnique save(CaracteristiqueTechnique caracteristique) {
        return caracteristiqueTechniqueRepository.save(caracteristique);
    }

    public void deleteById(Long id) {
        caracteristiqueTechniqueRepository.deleteById(id);
    }
}
