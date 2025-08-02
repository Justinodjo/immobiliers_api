package com.immoservice.immobiliers.fakers;
import com.github.javafaker.Faker;
import com.immoservice.immobiliers.entities.*;
import com.immoservice.immobiliers.repository.BiensImmobiliersRepository;
import com.immoservice.immobiliers.repository.InterventionRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Random;

@Component
public class InterventionDataLoader implements CommandLineRunner {

    private final InterventionRepository interventionRepository;
    private final BiensImmobiliersRepository biensImmobiliersRepository;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    public InterventionDataLoader(InterventionRepository interventionRepository, BiensImmobiliersRepository biensImmobiliersRepository) {
        this.interventionRepository = interventionRepository;
        this.biensImmobiliersRepository = biensImmobiliersRepository;
    }

    @Override
    public void run(String... args) {
        if (interventionRepository.count() == 0) {
            for (int i = 0; i < 20; i++) {
                Intervention intervention = new Intervention();
                intervention.setTypeIntervention(faker.company().profession());
                intervention.setDescription(faker.lorem().sentence());
                intervention.setEstimationCout(new BigDecimal(faker.number().numberBetween(10000, 100000)));
                intervention.setCoutReel(new BigDecimal(faker.number().numberBetween(8000, 120000)));

                // Dates fictives
                LocalDate dateSignalement = LocalDate.now().minusDays(faker.number().numberBetween(5, 30));
                LocalDate dateTraitement = dateSignalement.plusDays(faker.number().numberBetween(1, 10));
                intervention.setDateSignalement(Date.valueOf(dateSignalement));
                intervention.setDateTraitement(Date.valueOf(dateTraitement));

                // Statut et priorité aléatoires
                intervention.setStatut(StatutIntervention.values()[random.nextInt(StatutIntervention.values().length)]);
                intervention.setPriorite(NiveauPriorite.values()[random.nextInt(NiveauPriorite.values().length)]);

                // Lier à un bien existant (si existant)
                biensImmobiliersRepository.findAll().stream().findAny().ifPresent(intervention::setBiensImmobilers);

                // Enregistrement
                interventionRepository.save(intervention);
            }

            System.out.println("✅ 20 interventions fictives insérées avec succès !");
        }
    }
}
