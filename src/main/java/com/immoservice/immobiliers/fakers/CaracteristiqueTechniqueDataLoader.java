package com.immoservice.immobiliers.fakers;

import com.github.javafaker.Faker;
import com.immoservice.immobiliers.entities.CaracteristiqueTechnique;
import com.immoservice.immobiliers.entities.SousDetailsBiensImmobiliers;
import com.immoservice.immobiliers.enums.TypeCaracteristique;
import com.immoservice.immobiliers.repository.CaracteristiqueTechniqueRepository;
import com.immoservice.immobiliers.repository.SousDetailsBiensImmobiliersRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class CaracteristiqueTechniqueDataLoader implements CommandLineRunner {

    private final CaracteristiqueTechniqueRepository caracteristiqueTechniqueRepository;
    private final SousDetailsBiensImmobiliersRepository sousDetailsBiensImmobiliersRepository;
    private final Random random = new Random();
    private final Faker faker = new Faker();

    public CaracteristiqueTechniqueDataLoader(
            CaracteristiqueTechniqueRepository caracteristiqueTechniqueRepository,
            SousDetailsBiensImmobiliersRepository sousDetailsBiensImmobiliersRepository) {
        this.caracteristiqueTechniqueRepository = caracteristiqueTechniqueRepository;
        this.sousDetailsBiensImmobiliersRepository = sousDetailsBiensImmobiliersRepository;
    }

    @Override
    public void run(String... args) {
        if (caracteristiqueTechniqueRepository.count() == 0) {
            // Récupérer les sous-détails depuis la base de données
            List<SousDetailsBiensImmobiliers> sousDetailsList = sousDetailsBiensImmobiliersRepository.findAll();

            for (int i = 0; i < 10; i++) {
                CaracteristiqueTechnique caracteristique = new CaracteristiqueTechnique();

                // Assignation aléatoire du type de caractéristique
                TypeCaracteristique typeCaracteristique = TypeCaracteristique.values()[random
                        .nextInt(TypeCaracteristique.values().length)];
                caracteristique.setTypeCaracteristique(typeCaracteristique);

                // Possède aléatoire
                caracteristique.setPossede(faker.bool().bool());

                if (!sousDetailsList.isEmpty()) {
                    SousDetailsBiensImmobiliers detachedSousDetail = sousDetailsList
                            .get(random.nextInt(sousDetailsList.size()));

                    // 🔁 Recharge pour être sûr qu’elle est attachée au contexte JPA
                    SousDetailsBiensImmobiliers attachedSousDetail = sousDetailsBiensImmobiliersRepository
                            .findById(detachedSousDetail.getId())
                            .orElseThrow(() -> new RuntimeException("Sous-détail non trouvé"));

                    caracteristique.setSousDetailsBiensImmobiliers(attachedSousDetail);
                }

                // Sauvegarde de la caractéristique technique
                caracteristiqueTechniqueRepository.save(caracteristique);
            }

            System.out.println("✅ 10 enregistrements CaracteristiqueTechnique ajoutés !");
        }
    }
}
