// package com.immoservice.immobiliers.fakers;

// import java.util.List;

// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

// import com.immoservice.immobiliers.entities.SousDetailsBiensImmobiliers;
// import com.immoservice.immobiliers.entities.TypeSousDetailsBiens;
// import com.immoservice.immobiliers.repository.SousDetailsBiensImmobiliersRepository;

// import java.util.Arrays;
// import java.util.Random;

// @Component
// public class SousDetailsBiensImmobiliersDataLoader implements CommandLineRunner {

//     private final SousDetailsBiensImmobiliersRepository sousDetailsRepo;
//     private final Random random = new Random();

//     public SousDetailsBiensImmobiliersDataLoader(SousDetailsBiensImmobiliersRepository sousDetailsRepo) {
//         this.sousDetailsRepo = sousDetailsRepo;
//     }

//     @Override
//     public void run(String... args) throws Exception {
//         if (sousDetailsRepo.count() == 0) {
//             List<TypeSousDetailsBiens> types = Arrays.asList(TypeSousDetailsBiens.values());

//             for (int i = 0; i < 10; i++) { // Crée 10 sous-détails aléatoires
//                 SousDetailsBiensImmobiliers s = new SousDetailsBiensImmobiliers();
//                 TypeSousDetailsBiens randomType = types.get(random.nextInt(types.size()));

//                 s.setTypeSousDetailsBiens(randomType);
//                 s.setDescription("Description pour " + randomType.name().replace("_", " ").toLowerCase());
//                 s.setNombre(1 + random.nextInt(5)); // nombre entre 1 et 5
//                 s.setSuperficie(5 + random.nextDouble() * 45); // superficie entre 5 et 50 m²

//                 sousDetailsRepo.save(s);
//             }
//             System.out.println("✅ Sous-détails aléatoires insérés.");
//         } else {
//             System.out.println("ℹ️ Sous-détails déjà présents en base.");
//         }
//     }
// }
