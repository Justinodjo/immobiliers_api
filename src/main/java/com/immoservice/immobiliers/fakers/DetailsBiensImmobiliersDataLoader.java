// package com.immoservice.immobiliers.fakers;

// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Random;
// import java.util.Set;

// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import com.github.javafaker.Faker;
// import com.immoservice.immobiliers.entities.BiensImmobiliers;
// import com.immoservice.immobiliers.entities.CaracteristiqueTechnique;
// import com.immoservice.immobiliers.entities.DetailsBiensImmobiliers;
// import com.immoservice.immobiliers.entities.Fichier;
// import com.immoservice.immobiliers.entities.SousDetailsBiensImmobiliers;
// import com.immoservice.immobiliers.entities.TypeDetailsBiens;
// import com.immoservice.immobiliers.enums.TypeCaracteristique;
// import com.immoservice.immobiliers.repository.BiensImmobiliersRepository;
// import com.immoservice.immobiliers.repository.DetailsBiensImmobiliersRepository;
// import com.immoservice.immobiliers.repository.FichierRepository;
// import com.immoservice.immobiliers.repository.SousDetailsBiensImmobiliersRepository;

// import jakarta.persistence.EntityManager;
// import jakarta.persistence.PersistenceContext;

// @Component
// public class DetailsBiensImmobiliersDataLoader implements CommandLineRunner {

//     private final DetailsBiensImmobiliersRepository detailsRepository;
//     private final BiensImmobiliersRepository biensRepository;
//     private final SousDetailsBiensImmobiliersRepository sousDetailsRepository;
//     private final FichierRepository fichierRepository;

//     @PersistenceContext
//     private EntityManager entityManager;

//     private final Random random = new Random();
//     private final Faker faker = new Faker();

//     public DetailsBiensImmobiliersDataLoader(
//             DetailsBiensImmobiliersRepository detailsRepository,
//             BiensImmobiliersRepository biensRepository,
//             SousDetailsBiensImmobiliersRepository sousDetailsRepository,
//             FichierRepository fichierRepository) {
//         this.detailsRepository = detailsRepository;
//         this.biensRepository = biensRepository;
//         this.sousDetailsRepository = sousDetailsRepository;
//         this.fichierRepository = fichierRepository;
//     }

//     @Override
//     @Transactional
//     public void run(String... args) {
//         if (detailsRepository.count() == 0) {
//             List<BiensImmobiliers> biens = biensRepository.findAll();
//             List<SousDetailsBiensImmobiliers> sousDetails = sousDetailsRepository.findAll();
//             List<Fichier> fichiers = fichierRepository.findAll();

//             for (int i = 0; i < 10; i++) {
//                 DetailsBiensImmobiliers detail = new DetailsBiensImmobiliers();

//                 // Vérification du type de détail
//                 detail.setTypeDetailsBiens(
//                         TypeDetailsBiens.values()[random.nextInt(TypeDetailsBiens.values().length)]);
//                 detail.setNombre(faker.number().numberBetween(1, 5));
//                 detail.setDescription(faker.lorem().sentence());

//                 // Gestion des fichiers
//                 if (!fichiers.isEmpty()) {
//                     Fichier fichier = fichiers.get(random.nextInt(fichiers.size()));
//                     detail.setFichier(fichier);
//                     detail.setFichiers(Collections.singletonList(fichier)); // Utilise un seul fichier
//                 }

//                 // Lier à un bien immobilier
//                 if (!biens.isEmpty()) {
//                     detail.setBiensImmobilers(biens.get(random.nextInt(biens.size())));
//                 }

//                 // Gestion des sous-détails
//                 if (!sousDetails.isEmpty()) {
//                     int nbSousDetails = 1 + random.nextInt(4);
//                     Set<SousDetailsBiensImmobiliers> sousDetailsSelectionnes = new HashSet<>();
//                     while (sousDetailsSelectionnes.size() < nbSousDetails) {
//                         sousDetailsSelectionnes.add(sousDetails.get(random.nextInt(sousDetails.size())));
//                     }
//                     // Assigner des caractéristiques techniques
//                     for (SousDetailsBiensImmobiliers sousDetail : sousDetailsSelectionnes) {
//                         Set<TypeCaracteristique> typeCaracteristiques = getRandomEnumValues(TypeCaracteristique.class,
//                                 2);
//                         for (TypeCaracteristique tc : typeCaracteristiques) {
//                             boolean exists = sousDetail.getCaracteristiques().stream()
//                                     .anyMatch(c -> c.getTypeCaracteristique() == tc);

//                             if (!exists) {
//                                 CaracteristiqueTechnique caract = new CaracteristiqueTechnique();
//                                 caract.setPossede(random.nextBoolean());
//                                 caract.setTypeCaracteristique(tc);
//                                 caract.setSousDetailsBiensImmobiliers(sousDetail);

//                                 // Utiliser la méthode addCaracteristique pour ajouter la caractéristique
//                                 sousDetail.addCaracteristique(caract);
//                             }
//                         }
//                     }

//                     // Réattacher les sous-détails avant d'ajouter à la relation
//                     detail.setSousDetailsBiensImmobiliers(new ArrayList<>(sousDetailsSelectionnes));
//                     for (SousDetailsBiensImmobiliers sousDetail : sousDetailsSelectionnes) {
//                         sousDetail = entityManager.merge(sousDetail); // Réattacher l'entité avant d'ajouter à la
//                                                                       // relation
//                         sousDetail.setDetailsBiensImmobiliers(detail);
//                         detail.getSousDetailsBiensImmobiliers().add(sousDetail);
//                     }
//                 }

//                 detailsRepository.save(detail);
//             }

//             System.out.println("✅ 10 enregistrements DetailsBiensImmobiliers ajoutés !");
//         }
//     }

//     // Méthode pour obtenir des valeurs aléatoires d'un enum
//     private <E extends Enum<E>> Set<E> getRandomEnumValues(Class<E> enumClass, int numberOfValues) {
//         E[] enumConstants = enumClass.getEnumConstants();
//         Set<E> randomEnumValues = new HashSet<>();
//         numberOfValues = Math.min(numberOfValues, enumConstants.length); // Limite le nombre de valeurs à la taille de
//                                                                          // l'enum
//         while (randomEnumValues.size() < numberOfValues) {
//             randomEnumValues.add(enumConstants[random.nextInt(enumConstants.length)]);
//         }
//         return randomEnumValues;
//     }
// }
