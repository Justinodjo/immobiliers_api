// package com.immoservice.immobiliers.fakers;

// import java.util.ArrayList;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Random;
// import java.util.Set;

// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

// import com.github.javafaker.Faker;
// import com.immoservice.immobiliers.entities.Activite;
// import com.immoservice.immobiliers.entities.Arrondissement;
// import com.immoservice.immobiliers.entities.BiensImmobiliers;
// import com.immoservice.immobiliers.entities.Commune;
// import com.immoservice.immobiliers.entities.Departement;
// import com.immoservice.immobiliers.entities.DetailsBiensImmobiliers;
// import com.immoservice.immobiliers.entities.Equipements;
// import com.immoservice.immobiliers.entities.Fichier;
// import com.immoservice.immobiliers.entities.LanguePersonnel;
// import com.immoservice.immobiliers.entities.ModePaiement;
// import com.immoservice.immobiliers.entities.Securite;
// import com.immoservice.immobiliers.entities.Service;
// import com.immoservice.immobiliers.entities.SousDetailsBiensImmobiliers;
// import com.immoservice.immobiliers.entities.TypeBiensImmobiliere;
// import com.immoservice.immobiliers.entities.UserInfo;
// import com.immoservice.immobiliers.enums.TypeUnite;
// import com.immoservice.immobiliers.repository.ArrondissementRepository;
// import com.immoservice.immobiliers.repository.BiensImmobiliersRepository;
// import com.immoservice.immobiliers.repository.CommuneRepository;
// import com.immoservice.immobiliers.repository.DepartementRepository;
// import com.immoservice.immobiliers.repository.DetailsBiensImmobiliersRepository;
// import com.immoservice.immobiliers.repository.EquipementsRepository;
// import com.immoservice.immobiliers.repository.FichierRepository;
// import com.immoservice.immobiliers.repository.SousDetailsBiensImmobiliersRepository;
// import com.immoservice.immobiliers.repository.TypeBiensImmobiliereRepository;
// import com.immoservice.immobiliers.repository.UserInfoRepository;

// import jakarta.transaction.Transactional;

// @Component
// public class BiensImmobiliersDataLoader implements CommandLineRunner {

//     private final DetailsBiensImmobiliersRepository detailsBiensImmobiliersRepository;
//     private final BiensImmobiliersRepository biensImmobiliersRepository;
//     private final UserInfoRepository userInfoRepository;
//     private final TypeBiensImmobiliereRepository typeBiensImmobiliereRepository;
//     private final FichierRepository fichierRepository;
//     private final DepartementRepository departementRepository;
//     private final CommuneRepository communeRepository;
//     private final ArrondissementRepository arrondissementRepository;
//     private final EquipementsRepository equipementsRepository;
//     private final SousDetailsBiensImmobiliersRepository sousDetailsBiensImmobiliersRepository;

//     private final Faker faker = new Faker();
//     private final Random random = new Random();

//     public BiensImmobiliersDataLoader(UserInfoRepository userInfoRepository,
//             BiensImmobiliersRepository biensImmobiliersRepository,
//             DetailsBiensImmobiliersRepository detailsBiensImmobiliersRepository,
//             TypeBiensImmobiliereRepository typeBiensImmobiliereRepository, FichierRepository fichierRepository,
//             DepartementRepository departementRepository,
//             CommuneRepository communeRepository,
//             ArrondissementRepository arrondissementRepository, EquipementsRepository equipementsRepository,
//             SousDetailsBiensImmobiliersRepository sousDetailsBiensImmobiliersRepository) {
//         this.userInfoRepository = userInfoRepository;
//         this.biensImmobiliersRepository = biensImmobiliersRepository;
//         this.sousDetailsBiensImmobiliersRepository = sousDetailsBiensImmobiliersRepository;
//         this.detailsBiensImmobiliersRepository = detailsBiensImmobiliersRepository;
//         this.typeBiensImmobiliereRepository = typeBiensImmobiliereRepository;
//         this.fichierRepository = fichierRepository;
//         this.departementRepository = departementRepository;
//         this.communeRepository = communeRepository;
//         this.arrondissementRepository = arrondissementRepository;
//         this.equipementsRepository = equipementsRepository;
//     }

//     @Override
//     @Transactional
//     public void run(String... args) {
//         List<UserInfo> utilisateurs = userInfoRepository.findAll();
//         List<TypeBiensImmobiliere> types = typeBiensImmobiliereRepository.findAll();
//         List<DetailsBiensImmobiliers> details = detailsBiensImmobiliersRepository.findAll();
//         List<Arrondissement> arrondissements = arrondissementRepository.findAll();
//         // List<Equipements> equipements = equipementsRepository.findAll();
//         List<SousDetailsBiensImmobiliers> sousDetailsList = sousDetailsBiensImmobiliersRepository.findAll();

//         if (utilisateurs.isEmpty() || types.isEmpty() || details.isEmpty()) {
//             System.out.println(
//                     "⚠️ Veuillez d'abord insérer des utilisateurs, des types de biens et des détails concernant le biens.");
//             return;
//         }
//         if (arrondissements.isEmpty()) {
//             System.out.println("⚠️ Veuillez d'abord insérer des départements, communes et arrondissements.");
//             return;
//         }
//         if (sousDetailsList.isEmpty()) {
//             System.out.println("⚠️ Veuillez insérer des sous-détails pour les biens immobiliers.");
//             return;
//         }
//         for (int i = 0; i < 20; i++) {

//             BiensImmobiliers bien = new BiensImmobiliers();
//             // Choisir un arrondissement au hasard
//             Arrondissement arrondissement = arrondissements.get(random.nextInt(arrondissements.size()));
//             Commune commune = arrondissement.getCommune();
//             Departement departement = commune.getDepartement();
//             bien.setNom(faker.company().name());
//             bien.setAdresse(faker.address().fullAddress());
//             bien.setSuperficie(30 + random.nextDouble() * 120); // entre 30 et 150 m²
//             double prix = 50000 + random.nextDouble() * (1000000 - 50000); // Prix entre 50 000 et 1 000 000
//             bien.setPrix(prix);
//             bien.setDescription(faker.lorem().paragraph(2));
//             // Initialisation aléatoire de 'typeUnite' (DAY, MONTH, WEEK)
//             TypeUnite[] typesUnite = TypeUnite.values(); // Liste des types d'unité
//             bien.setTypeUnite(typesUnite[random.nextInt(typesUnite.length)]); // Sélectionner un type d'unité aléatoire

//             // Attribuer true ou false aléatoirement
//             bien.setaVendre(random.nextBoolean());
//             bien.setStatus(random.nextBoolean());
//             bien.setUserInfo(utilisateurs.get(random.nextInt(utilisateurs.size())));
//             bien.setTypeBiensImmobiliere(types.get(random.nextInt(types.size())));
//             bien.setDetailsBiensImmobiliers(generateDetails(bien, details));

//             bien.setNote(random.nextDouble() * 4 + 1);

//             // === Génération d'équipements aléatoires ===
//             Equipements equipements = new Equipements();
//             equipements.setActivites(getRandomEnumValues(Activite.class, 3));
//             equipements.setModesPaiement(getRandomEnumValues(ModePaiement.class, 2));
//             equipements.setServices(getRandomEnumValues(Service.class, 3));
//             equipements.setSecurite(getRandomEnumValues(Securite.class, 2));
//             equipements.setLanguesPersonnel(getRandomEnumValues(LanguePersonnel.class, 3));

//             // 💥 Important : on sauve les équipements AVANT de les associer
//             // equipementsRepository.save(equipements);

//             // 👇 Maintenant qu’il est persistant, on peut l’associer
//             bien.setEquipements(equipements);
//             // Affecter les relations
//             bien.setArrondissement(arrondissement);
//             bien.setCommune(commune);
//             bien.setDepartement(departement);
//             // Ajout latitude / longitude aléatoires autour d'une zone (exemple Bénin)
//             double latitude = 6 + random.nextDouble() * 1; // entre 6 et 7
//             double longitude = 2 + random.nextDouble() * 1; // entre 2 et 3
//             bien.setLatitude(latitude);
//             bien.setLongitude(longitude);
//             String nom = faker.address().streetName();

//             // Étape 1 : enregistrer le bien
//             // biensImmobiliersRepository.save(bien);

//             // Images placeholders (2 par bien)
//             ArrayList<Fichier> fichiers = new ArrayList<>();
//             for (int j = 1; j <= 2; j++) {
//                 Fichier fichier = new Fichier();
//                 fichier.setNom("image_" + i + "_" + j + ".png");
//                 fichier.setOriginalName("original_" + nom);
//                 fichier.setTaille(1024L + random.nextInt(2048)); // taille aléatoire entre 1 et 3 Mo
//                 fichier.setContentType("image/png");
//                 fichier.setUrl("https://placehold.co/600x400?text=Maison+" + i + "+" + j);
//                 fichier.setTypeFichier("image");
//                 fichier.setUserInfo(bien.getUserInfo()); // Associer le fichier à l'utilisateur du bien
//                 fichierRepository.save(fichier); // explicit save if no cascade

//                 fichiers.add(fichier);
//             }

//             bien.setFichiers(fichiers);
//             System.out.println("Saving bien immobilier: " + bien.getNom());
//             biensImmobiliersRepository.save(bien);
//         }

//         System.out.println("✅ Données fictives BiensImmobiliers insérées avec succès!");
//     }

//     private List<DetailsBiensImmobiliers> generateDetails(BiensImmobiliers bien,
//             List<DetailsBiensImmobiliers> details) {
//         int numberOfDetails = random.nextInt(3) + 1; // Nombre aléatoire de détails (1 à 3)
//         List<DetailsBiensImmobiliers> bienDetails = new ArrayList<>();

//         for (int i = 0; i < numberOfDetails; i++) {
//             DetailsBiensImmobiliers detail = details.get(random.nextInt(details.size()));
//             detail.setBiensImmobilers(bien); // Associer le bien immobilier au détail
//             bienDetails.add(detail);
//         }

//         return bienDetails;
//     }

//     private <T extends Enum<?>> Set<T> getRandomEnumValues(Class<T> enumClass, int maxCount) {
//         T[] values = enumClass.getEnumConstants();
//         Set<T> selected = new HashSet<>();
//         int count = 1 + random.nextInt(Math.min(maxCount, values.length)); // entre 1 et maxCount
//         while (selected.size() < count) {
//             selected.add(values[random.nextInt(values.length)]);
//         }
//         return selected;
//     }
// }