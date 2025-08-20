package com.immoservice.immobiliers.init;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.immoservice.immobiliers.config.ReflectionsUtil;
import com.immoservice.immobiliers.entities.Permission;
import com.immoservice.immobiliers.entities.Role;
import com.immoservice.immobiliers.entities.TypeBiensImmobiliere;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.PermissionRepository;
import com.immoservice.immobiliers.repository.RoleRepository;
import com.immoservice.immobiliers.repository.TypeBiensImmobiliereRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;

import jakarta.persistence.Entity;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TypeBiensImmobiliereRepository typeBiensImmobiliereRepository;

    // @Autowired
    // private ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // 1️⃣ Créer les permissions pour la gestion des utilisateurs
        // 1️⃣ Créer les permissions pour la gestion des entités dynamiquement
        createPermissionsForEntities();
        // ==========================
        // 1️⃣ Création des permissions
        // ==========================

        // --- Gestion des utilisateurs ---
        createPermissionIfNotExists("CREATE_USER", "Créer un utilisateur");
        createPermissionIfNotExists("UPDATE_USER", "Modifier un utilisateur");
        createPermissionIfNotExists("DELETE_USER", "Supprimer un utilisateur");
        createPermissionIfNotExists("VIEW_USER", "Voir les utilisateurs");

        // --- Gestion système ---
        createPermissionIfNotExists("MANAGE_ROLES", "Gérer les rôles");
        createPermissionIfNotExists("MANAGE_PERMISSIONS", "Gérer les permissions");
        createPermissionIfNotExists("MANAGE_SYSTEM_SETTINGS", "Modifier les paramètres système");
        createPermissionIfNotExists("VIEW_AUDIT_LOGS", "Voir les logs d’audit");

        // --- Gestion immobilière ---
        createPermissionIfNotExists("MANAGE_PROPERTIES", "Gérer les biens immobiliers");
        createPermissionIfNotExists("MANAGE_PROPERTY_TYPES", "Gérer les types de biens");
        createPermissionIfNotExists("MANAGE_PROPERTY_DETAILS", "Modifier les détails d’un bien");
        createPermissionIfNotExists("VIEW_PROPERTIES", "Voir les biens immobiliers");
        createPermissionIfNotExists("VIEW_MY_PROPERTIES", "Voir mes biens");
        createPermissionIfNotExists("VIEW_MY_PROPERTY", "Voir mon bien");

        // --- Gestion des contrats ---
        createPermissionIfNotExists("MANAGE_CONTRACTS", "Gérer les contrats");
        createPermissionIfNotExists("VIEW_CONTRACTS", "Voir les contrats");
        createPermissionIfNotExists("VIEW_MY_CONTRACTS", "Voir mes contrats");
        createPermissionIfNotExists("VIEW_MY_CONTRACT", "Voir mon contrat");

        // --- Gestion des paiements ---
        createPermissionIfNotExists("MANAGE_PAYMENTS", "Gérer les paiements");
        createPermissionIfNotExists("VIEW_PAYMENTS", "Voir les paiements");
        createPermissionIfNotExists("VIEW_MY_PAYMENTS", "Voir mes paiements");

        // --- Interventions / Maintenance ---
        createPermissionIfNotExists("MANAGE_INTERVENTIONS", "Gérer les interventions");
        createPermissionIfNotExists("REQUEST_INTERVENTIONS", "Demander une intervention");
        createPermissionIfNotExists("VIEW_INTERVENTIONS", "Voir les interventions");
        createPermissionIfNotExists("VIEW_MY_INTERVENTIONS", "Voir mes interventions");
        createPermissionIfNotExists("MANAGE_MY_INTERVENTIONS", "Gérer mes interventions");

        // --- Tickets support ---
        createPermissionIfNotExists("MANAGE_TICKETS", "Gérer les tickets");
        createPermissionIfNotExists("CREATE_TICKET", "Créer un ticket");
        createPermissionIfNotExists("VIEW_TICKETS", "Voir les tickets");

        // --- Fournisseurs ---
        createPermissionIfNotExists("MANAGE_SUPPLIERS", "Gérer les fournisseurs");
        createPermissionIfNotExists("VIEW_SUPPLIERS", "Voir les fournisseurs");
        createPermissionIfNotExists("VIEW_MY_INVOICES", "Voir mes factures");
        createPermissionIfNotExists("UPLOAD_CERTIFICATES", "Uploader des certificats");
        createPermissionIfNotExists("VIEW_CLIENTS", "Voir les clients");
        // --- Notariat ---
        createPermissionIfNotExists("VIEW_TRANSACTIONS", "Voir les transactions");
        createPermissionIfNotExists("VIEW_ACTS", "Voir les actes notariés");
        createPermissionIfNotExists("MANAGE_DOCUMENTS", "Gérer les documents");
        createPermissionIfNotExists("VIEW_DOCUMENTS", "Voir les documents");

        // --- Rapports & Analytics ---
        createPermissionIfNotExists("VIEW_REPORTS", "Voir les rapports");
        createPermissionIfNotExists("VIEW_STATISTICS", "Voir les statistiques");

        // 2️⃣ Créer tous les rôles par défaut
        String[] roles = { "CLIENTS_ROLES", "LOCATAIRE_ROLES", "PROPRIETAIRE_ROLES", "ADMIN_ROLES", "MANAGER_ROLES",
                "SUPPLIER_ROLES",
                "NOTARY_ROLES", "SUPER_ADMIN_ROLES" };
        for (String nomRole : roles) {
            createRoleIfNotExists(nomRole);
        }
        // ==========================
        // 2️⃣ Assignation des permissions aux rôles
        // ==========================

        // SUPER ADMIN
        assignPermissionsToRole("SUPER_ADMIN_ROLES",
                "CREATE_USER", "UPDATE_USER", "DELETE_USER", "VIEW_USER",
                "MANAGE_ROLES", "MANAGE_PERMISSIONS", "MANAGE_SYSTEM_SETTINGS", "VIEW_AUDIT_LOGS",
                "MANAGE_PROPERTIES", "MANAGE_PROPERTY_TYPES", "MANAGE_PROPERTY_DETAILS", "VIEW_PROPERTIES",
                "MANAGE_CONTRACTS", "VIEW_CONTRACTS",
                "MANAGE_PAYMENTS", "VIEW_PAYMENTS",
                "MANAGE_INTERVENTIONS", "VIEW_INTERVENTIONS", "REQUEST_INTERVENTIONS",
                "MANAGE_TICKETS", "VIEW_TICKETS", "CREATE_TICKET",
                "MANAGE_SUPPLIERS", "VIEW_SUPPLIERS",
                "VIEW_TRANSACTIONS", "VIEW_ACTS", "MANAGE_DOCUMENTS", "VIEW_DOCUMENTS",
                "VIEW_REPORTS", "VIEW_STATISTICS");

        // ADMIN
        assignPermissionsToRole("ADMIN_ROLES",
                "CREATE_USER", "UPDATE_USER", "VIEW_USER",
                "MANAGE_PROPERTIES", "MANAGE_PROPERTY_TYPES", "MANAGE_PROPERTY_DETAILS", "VIEW_PROPERTIES",
                "MANAGE_CONTRACTS", "VIEW_CONTRACTS",
                "MANAGE_PAYMENTS", "VIEW_PAYMENTS",
                "MANAGE_INTERVENTIONS", "VIEW_INTERVENTIONS",
                "VIEW_REPORTS", "VIEW_STATISTICS");

        // MANAGER
        assignPermissionsToRole("MANAGER_ROLES",
                "VIEW_USER",
                "MANAGE_CONTRACTS", "VIEW_CONTRACTS",
                "MANAGE_PAYMENTS", "VIEW_PAYMENTS",
                "MANAGE_INTERVENTIONS", "VIEW_INTERVENTIONS", "REQUEST_INTERVENTIONS",
                "MANAGE_SUPPLIERS", "VIEW_SUPPLIERS",
                "MANAGE_TICKETS", "VIEW_TICKETS", "CREATE_TICKET",
                "VIEW_REPORTS", "VIEW_STATISTICS");

        // PROPRIETAIRE
        assignPermissionsToRole("PROPRIETAIRE_ROLES",
                "VIEW_MY_PROPERTIES", "VIEW_PROPERTIES",
                "VIEW_MY_PAYMENTS", "VIEW_PAYMENTS",
                "VIEW_MY_CONTRACTS", "VIEW_CONTRACTS",
                "REQUEST_INTERVENTIONS", "VIEW_TICKETS");

        // LOCATAIRE
        assignPermissionsToRole("LOCATAIRE_ROLES",
                "VIEW_MY_PROPERTY", "VIEW_PROPERTIES",
                "VIEW_MY_CONTRACT", "VIEW_CONTRACTS",
                "VIEW_MY_PAYMENTS", "VIEW_PAYMENTS",
                "CREATE_TICKET", "VIEW_TICKETS");

        // SUPPLIER
        assignPermissionsToRole("SUPPLIER_ROLES",
                "VIEW_MY_INTERVENTIONS", "MANAGE_MY_INTERVENTIONS",
                "VIEW_MY_INVOICES", "VIEW_MY_PAYMENTS",
                "VIEW_CLIENTS", "VIEW_PROPERTIES",
                "VIEW_DOCUMENTS", "UPLOAD_CERTIFICATES");

        // NOTARY
        assignPermissionsToRole("NOTARY_ROLES",
                "VIEW_TRANSACTIONS", "VIEW_CONTRACTS",
                "VIEW_ACTS", "MANAGE_DOCUMENTS", "VIEW_DOCUMENTS",
                "VIEW_CLIENTS", "VIEW_REPORTS");

        // 4️⃣ Créer un utilisateur par défaut
        createDefaultAdmin();
        // 5️⃣ , 6️⃣ ,7️⃣,8️⃣, 9️⃣,🔟 Initialiser les types de biens immobiliers
        initTypeBiensImmobiliere();
        System.out.println(">>> Initialisation des données terminée.");
    }

    // Générer les permissions pour chaque entité automatiquement
    private void createPermissionsForEntities() {
        Set<Class<?>> entities = findEntities();

        for (Class<?> entity : entities) {
            if (entity.isAnnotationPresent(Entity.class)) {
                String entityName = entity.getSimpleName().toUpperCase();
                createPermissionIfNotExists("CREATE_" + entityName, "Créer un " + entityName.toLowerCase());
                createPermissionIfNotExists("READ_" + entityName, "Lire un " + entityName.toLowerCase());
                createPermissionIfNotExists("UPDATE_" + entityName, "Modifier un " + entityName.toLowerCase());
                createPermissionIfNotExists("DELETE_" + entityName, "Supprimer un " + entityName.toLowerCase());
            }
        }
    }

    // Trouver toutes les classes annotées avec @Entity
    private Set<Class<?>> findEntities() {
        Set<Class<?>> entities = new HashSet<>();
        String packageName = "com.immoservice.immobiliers.entities"; // Le package où se trouvent tes entités
        try {
            Set<Class<?>> classes = ReflectionsUtil.getClassesInPackage(packageName);
            for (Class<?> clazz : classes) {
                if (clazz.isAnnotationPresent(Entity.class)) {
                    entities.add(clazz);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entities;
    }

    private void createPermissionIfNotExists(String nom, String description) {
        Optional<Permission> existing = permissionRepository.findByNom(nom);
        if (existing.isEmpty()) {
            Permission p = new Permission();
            p.setNom(nom);
            p.setDescription(description);
            permissionRepository.save(p);
            System.out.println(">>> Permission ajoutée : " + nom);
        } else {
            System.out.println(">>> Permission déjà existante : " + nom);
        }
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

    private void assignPermissionsToRole(String nomRole, String... nomPermissions) {
        // Récupérer le rôle depuis la base de données
        Role role = roleRepository.findByNomRole(nomRole).orElseThrow(() -> new RuntimeException("Role non trouvé"));

        // Créer un ensemble de permissions
        HashSet<Permission> perms = new HashSet<>();
        for (String nomPerm : nomPermissions) {
            // Récupérer chaque permission par son nom
            Permission p = permissionRepository.findByNom(nomPerm)
                    .orElseThrow(() -> new RuntimeException("Permission non trouvée"));
            perms.add(p);
        }

        // Assigner les permissions au rôle
        role.setPermissions(perms);

        // Sauvegarder le rôle avec les permissions mises à jour
        roleRepository.save(role);

        // Afficher une confirmation
        System.out.println(">>> Permissions assignées au rôle " + nomRole);
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
