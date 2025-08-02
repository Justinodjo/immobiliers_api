package com.immoservice.immobiliers.fakers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
import com.immoservice.immobiliers.entities.Arrondissement;
import com.immoservice.immobiliers.entities.BiensImmobiliers;
import com.immoservice.immobiliers.entities.Commune;
import com.immoservice.immobiliers.entities.Departement;
import com.immoservice.immobiliers.entities.DetailsBiensImmobiliers;
import com.immoservice.immobiliers.entities.Fichier;
import com.immoservice.immobiliers.entities.TypeBiensImmobiliere;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.ArrondissementRepository;
import com.immoservice.immobiliers.repository.BiensImmobiliersRepository;
import com.immoservice.immobiliers.repository.CommuneRepository;
import com.immoservice.immobiliers.repository.DepartementRepository;
import com.immoservice.immobiliers.repository.DetailsBiensImmobiliersRepository;
import com.immoservice.immobiliers.repository.FichierRepository;
import com.immoservice.immobiliers.repository.TypeBiensImmobiliereRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;

@Component
public class BiensImmobiliersDataLoader implements CommandLineRunner {

    private final DetailsBiensImmobiliersRepository detailsBiensImmobiliersRepository;
    private final BiensImmobiliersRepository biensImmobiliersRepository;
    private final UserInfoRepository userInfoRepository;
    private final TypeBiensImmobiliereRepository typeBiensImmobiliereRepository;
    private final FichierRepository fichierRepository;
    private final DepartementRepository departementRepository;
    private final CommuneRepository communeRepository;
    private final ArrondissementRepository arrondissementRepository;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    public BiensImmobiliersDataLoader(UserInfoRepository userInfoRepository,
            BiensImmobiliersRepository biensImmobiliersRepository,
            DetailsBiensImmobiliersRepository detailsBiensImmobiliersRepository,
            TypeBiensImmobiliereRepository typeBiensImmobiliereRepository, FichierRepository fichierRepository,
            DepartementRepository departementRepository,
            CommuneRepository communeRepository,
            ArrondissementRepository arrondissementRepository) {
        this.userInfoRepository = userInfoRepository;
        this.biensImmobiliersRepository = biensImmobiliersRepository;
        this.detailsBiensImmobiliersRepository = detailsBiensImmobiliersRepository;
        this.typeBiensImmobiliereRepository = typeBiensImmobiliereRepository;
        this.fichierRepository = fichierRepository;
        this.departementRepository = departementRepository;
        this.communeRepository = communeRepository;
        this.arrondissementRepository = arrondissementRepository;
    }

    @Override
    public void run(String... args) {
        List<UserInfo> utilisateurs = userInfoRepository.findAll();
        List<TypeBiensImmobiliere> types = typeBiensImmobiliereRepository.findAll();
        List<DetailsBiensImmobiliers> details = detailsBiensImmobiliersRepository.findAll();
        List<Arrondissement> arrondissements = arrondissementRepository.findAll();

        if (utilisateurs.isEmpty() || types.isEmpty() || details.isEmpty()) {
            System.out.println(
                    "⚠️ Veuillez d'abord insérer des utilisateurs, des types de biens et des détails concernant le biens.");
            return;
        }
        if (arrondissements.isEmpty()) {
            System.out.println("⚠️ Veuillez d'abord insérer des départements, communes et arrondissements.");
            return;
        }

        for (int i = 0; i < 20; i++) {
            BiensImmobiliers bien = new BiensImmobiliers();
            // Choisir un arrondissement au hasard
            Arrondissement arrondissement = arrondissements.get(random.nextInt(arrondissements.size()));
            Commune commune = arrondissement.getCommune();
            Departement departement = commune.getDepartement();
            bien.setNom(faker.company().name());
            bien.setAdresse(faker.address().fullAddress());
            bien.setSuperficie(30 + random.nextDouble() * 120); // entre 30 et 150 m²
            bien.setPrix(50000 + random.nextInt(950000)); // entre 50 000 et 1 000 000
            bien.setDescription(faker.lorem().paragraph(2));
            bien.setStatus(random.nextBoolean());
            bien.setUserInfo(utilisateurs.get(random.nextInt(utilisateurs.size())));
            bien.setTypeBiensImmobiliere(types.get(random.nextInt(types.size())));
            bien.setDetailsBiensImmobiliers(details.get(random.nextInt(details.size()))); // si nécessaire
            bien.setNote(random.nextDouble() * 4 + 1);
            // Affecter les relations
            bien.setArrondissement(arrondissement);
            bien.setCommune(commune);
            bien.setDepartement(departement);
            // Ajout latitude / longitude aléatoires autour d'une zone (exemple Bénin)
            double latitude = 6 + random.nextDouble() * 1; // entre 6 et 7
            double longitude = 2 + random.nextDouble() * 1; // entre 2 et 3
            bien.setLatitude(latitude);
            bien.setLongitude(longitude);
            String nom = faker.address().streetName();

            // Étape 1 : enregistrer le bien
            biensImmobiliersRepository.save(bien);

            // Images placeholders (2 par bien)
            ArrayList<Fichier> fichiers = new ArrayList<>();
            for (int j = 1; j <= 2; j++) {
                Fichier fichier = new Fichier();
                fichier.setNom("image_" + i + "_" + j + ".png");
                fichier.setOriginalName("original_" + nom);
                fichier.setTaille(1024L + random.nextInt(2048)); // taille aléatoire entre 1 et 3 Mo
                fichier.setContentType("image/png");
                fichier.setUrl("https://placehold.co/600x400?text=Maison+" + i + "+" + j);
                fichier.setTypeFichier("image");
                fichier.setUserInfo(bien.getUserInfo()); // Associer le fichier à l'utilisateur du bien
                fichierRepository.save(fichier); // explicit save if no cascade

                fichiers.add(fichier);
            }

            bien.setFichiers(fichiers);
            biensImmobiliersRepository.save(bien);
        }

        System.out.println("✅ Données fictives BiensImmobiliers insérées avec succès!");
    }
}