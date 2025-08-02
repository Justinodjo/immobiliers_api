package com.immoservice.immobiliers.init;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.immoservice.immobiliers.entities.Role;
import com.immoservice.immobiliers.entities.TypeBiensImmobiliere;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.RoleRepository;
import com.immoservice.immobiliers.repository.TypeBiensImmobiliereRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TypeBiensImmobiliereRepository typeBiensImmobiliereRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Créer tous les rôles par défaut
        String[] roles = { "LOCATAIRE_ROLES", "PROPRIETAIRE_ROLES", "ADMIN_ROLES", "MANAGER_ROLES", "SUPPLIER_ROLES",
                "NOTARY_ROLES", "SUPER_ADMIN_ROLES" };
        for (String nomRole : roles) {
            createRoleIfNotExists(nomRole);
        }

        // Créer un utilisateur par défaut
        createDefaultAdmin();
        // Initialiser les types de biens immobiliers
        initTypeBiensImmobiliere();
        System.out.println(">>> Initialisation des données terminée.");
    }

    private void createRoleIfNotExists(String nomRole) {
        if (roleRepository.findByNomRole(nomRole).isEmpty()) {
            Role role = new Role();
            role.setNomRole(nomRole);
            roleRepository.save(role);
            System.out.println(">>> Rôle ajouté automatiquement : " + nomRole);
        } else {
            System.out.println(">>> Rôle déjà existant : " + nomRole);
        }
    }

    private void createDefaultAdmin() {
        String defaultEmail = "admin@admin.com";
        Optional<UserInfo> existingAdmin = userInfoRepository.findByEmail(defaultEmail);
        if (existingAdmin.isEmpty()) {
            UserInfo admin = new UserInfo();
            admin.setName("ADMIN");
            admin.setSecondName("Admin");
            admin.setEmail(defaultEmail);
            admin.setPassword(passwordEncoder.encode("123456")); // mot de passe par défaut
            admin.setRoles(new HashSet<>(Arrays.asList(
                    roleRepository.findByNomRole("SUPER_ADMIN_ROLES").get())));
            userInfoRepository.save(admin);
            System.out.println(">>> Utilisateur admin créé avec succès !");
        } else {
            System.out.println(">>> Utilisateur admin déjà existant.");
        }
    }

    private void initTypeBiensImmobiliere() {
        String[][] types = {
                { "Résidentiel",
                        "Biens immobiliers destinés à l'habitation, tels que des maisons, des appartements, des villas, des condos, etc. Ces propriétés sont conçues pour offrir un espace de vie confortable et sécurisé pour les familles, les couples, les célibataires, etc." },
                { "Commercial",
                        "Biens immobiliers destinés à des activités commerciales, tels que des bureaux, des magasins, des entrepôts, etc. Ces propriétés sont conçues pour accueillir des entreprises, des commerces, des services,etc" },
                { "Industriel",
                        "Biens immobiliers destinés à des activités industrielles, tels que des usines, des entrepôts, des ateliers, des dépôts, etc. Ces propriétés sont conçues pour répondre aux besoins spécifiques des entreprises industrielles." },
                { "Agricole",
                        "Biens immobiliers destinés à des activités agricoles, tels que des terres agricoles, des fermes, des vignobles, des élevages, etc. Ces propriétés sont conçues pour produire des produits agricoles, élever des animaux, etc." },
                { "Investissement",
                        "Biens immobiliers achetés dans le but de générer des revenus locatifs ou des plus-values à long terme. Ces propriétés sont souvent louées à des locataires, ou revendues à un prix plus élevé que le prix d'achat." }
        };

        for (String[] type : types) {
            String nom = type[0];
            String desc = type[1];

            Optional<TypeBiensImmobiliere> optionalType = typeBiensImmobiliereRepository.findByNomTypeBiensImmo(nom);
            if (!optionalType.isPresent()) {
                TypeBiensImmobiliere newType = new TypeBiensImmobiliere();
                newType.setNomTypeBiensImmo(nom);
                newType.setDescriptif(desc);
                typeBiensImmobiliereRepository.save(newType);
                System.out.println(">>> Type bien ajouté : " + nom);
            } else {
                System.out.println(">>> Type bien déjà existant : " + nom);
            }
        }
    }

}
