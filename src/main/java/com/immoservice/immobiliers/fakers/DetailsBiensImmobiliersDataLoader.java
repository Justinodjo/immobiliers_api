package com.immoservice.immobiliers.fakers;

import com.immoservice.immobiliers.entities.*;
import com.immoservice.immobiliers.repository.*;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class DetailsBiensImmobiliersDataLoader implements CommandLineRunner {

    private final DetailsBiensImmobiliersRepository detailsRepository;
    private final BiensImmobiliersRepository biensRepository;
    private final SousDetailsBiensImmobiliersRepository sousDetailsRepository;
    private final FichierRepository fichierRepository;

    private final Random random = new Random();
    private final Faker faker = new Faker();

    public DetailsBiensImmobiliersDataLoader(
            DetailsBiensImmobiliersRepository detailsRepository,
            BiensImmobiliersRepository biensRepository,
            SousDetailsBiensImmobiliersRepository sousDetailsRepository,
            FichierRepository fichierRepository) {
        this.detailsRepository = detailsRepository;
        this.biensRepository = biensRepository;
        this.sousDetailsRepository = sousDetailsRepository;
        this.fichierRepository = fichierRepository;
    }

    @Override
    public void run(String... args) {
        if (detailsRepository.count() == 0) {
            List<BiensImmobiliers> biens = biensRepository.findAll();
            List<SousDetailsBiensImmobiliers> sousDetails = sousDetailsRepository.findAll();
            List<Fichier> fichiers = fichierRepository.findAll();

            for (int i = 0; i < 10; i++) {
                DetailsBiensImmobiliers detail = new DetailsBiensImmobiliers();

                detail.setTypeDetailsBiens(TypeDetailsBiens.values()[random.nextInt(TypeDetailsBiens.values().length)]);
                detail.setNombre(faker.number().numberBetween(1, 5));
                detail.setDescription(faker.lorem().sentence());

                if (!fichiers.isEmpty()) {
                    detail.setFichier(fichiers.get(random.nextInt(fichiers.size())));
                    detail.setFichiers(Collections.singletonList(fichiers.get(random.nextInt(fichiers.size()))));
                }

                if (!biens.isEmpty()) {
                    detail.setBiensImmobilers(biens.get(random.nextInt(biens.size())));
                }

                if (!sousDetails.isEmpty()) {
                    detail.setSousDetailsBiensImmobiliers(sousDetails.get(random.nextInt(sousDetails.size())));
                }

                detailsRepository.save(detail);
            }

            System.out.println("✅ 10 enregistrements DetailsBiensImmobiliers ajoutés !");
        }
    }
}
