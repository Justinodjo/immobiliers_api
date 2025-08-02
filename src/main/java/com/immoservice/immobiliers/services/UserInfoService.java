package com.immoservice.immobiliers.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.immoservice.immobiliers.entities.AuthRequest;
import com.immoservice.immobiliers.entities.Role;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.RoleRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;
import com.immoservice.immobiliers.response.AuthResponse;

@Service
public class UserInfoService implements UserDetailsService {
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JavaMailSender mailSender;

    // Méthode pour trouver un utilisateur par email
    // @Cacheable("userByEmail")
    public Optional<UserInfo> findUserByEmail(String email) {
        return userInfoRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> userInfo = userInfoRepository.findByName(username);
        return userInfo.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public void sendWelcomeEmailWithReset(UserInfo userInfo) {
        String subject = "Bienvenue chez MainsDorPlus";
        String resetUrl = "https://www.mainsdorplus:8081/reset-password";

        // Encoder le mot de passe
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));

        String message = "Bonjour " + userInfo.getName() + ",\n\nBienvenue chez MainsDorPlus !\n\n"
                + "Voici votre mot de passe temporaire : " + userInfo.getPassword() + "\n\n"
                + "Pour des raisons de sécurité, veuillez réinitialiser votre mot de passe en cliquant sur le lien suivant : "
                + resetUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(userInfo.getEmail());
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }

    public UserInfo addUser(UserInfo userInfo) {
        // Vérifier si un utilisateur avec le même email existe déjà
        if (userInfoRepository.findByEmail(userInfo.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà : " + userInfo.getEmail());
        }

        // Vérifier si un utilisateur avec le même nom existe déjà
        if (userInfoRepository.findByName(userInfo.getName()).isPresent()) {
            throw new IllegalArgumentException("Un utilisateur avec ce nom existe déjà : " + userInfo.getName());
        }

        // Encoder le mot de passe
        // userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));

        Collection<Role> validatedRoles = new ArrayList<>();
        if (!userInfo.getRoles().isEmpty()) {
            // Vérifiez et associez les rôles existants
            for (Role role : userInfo.getRoles()) {
                if (role.getNomRole() == null || role.getNomRole().isEmpty()) {
                    throw new IllegalArgumentException("Le rôle de l'utilisateur ne peut pas être vide ou null.");
                }
                Optional<Role> existingRole = roleRepository.findByNomRole(role.getNomRole());
                if (existingRole.isPresent()) {
                    validatedRoles.add(existingRole.get());
                } else {
                    throw new IllegalArgumentException("Rôle non trouvé : " + role.getNomRole());
                }
            }
        } else {
            // Si aucun rôle n'est spécifié, récupérez le rôle par défaut "CLIENTS_ROLES"
            Optional<Role> defaultRole = roleRepository.findByNomRole("CLIENTS_ROLES");
            if (defaultRole.isPresent()) {
                validatedRoles.add(defaultRole.get());
            } else {
                throw new IllegalArgumentException("Rôle par défaut non trouvé : CLIENTS_ROLES");
            }
        }

        userInfo.setRoles(validatedRoles);

        // Encoder le mot de passe
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));

        UserInfo savedUserInfo = userInfoRepository.save(userInfo);
        // Envoyer l'email de bienvenue avec le mot de passe et l'URL de
        // réinitialisation
        sendWelcomeEmailWithReset(savedUserInfo);

        return savedUserInfo;
    }

    public AuthResponse login(AuthRequest authRequest) {
        // Rechercher l'utilisateur par son nom ou son email
        final UserInfo user = userInfoRepository.findByNameOrEmail(authRequest.getUserName(), authRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Vérifier si le mot de passe fourni correspond à celui de la base de données
        if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            // Garder les rôles en tant que GrantedAuthority
            Collection<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getNomRole())) // Créer des SimpleGrantedAuthority
                    .collect(Collectors.toList());

            // Assurer que les rôles sont bien présents
            if (authorities.isEmpty()) {
                throw new IllegalArgumentException("No roles found for user: " + user.getName());
            }
            String fichierPrincipal = user.getFichier() != null ? user.getFichier().getNom() : null;

            // Liste des fichiers (si nécessaire)
            List<String> fichiers = user.getFichiers().stream()
                    .map(fichier -> fichier.getNom()) // Extrait le nom du fichier en String
                    .collect(Collectors.toList());
            user.getFichiers().size();
            // Générer le token JWT
            String token = jwtService.generateToken(user.getName(), user.getEmail(), authorities);

            // Retourner la réponse d'authentification avec le token et les informations
            // utilisateur
            return new AuthResponse(user.getId(), token, user.getName(), user.getSecondName(), user.getNumero(),
                    user.getEmail(), fichierPrincipal, fichiers, authorities);
        } else {
            // Lancer une exception si le mot de passe ne correspond pas
            throw new UsernameNotFoundException("Invalid username or password");
        }
    }

    public List<UserInfo> getUsersByRole(String roleName) {
        return userInfoRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getNomRole().equals(roleName)))
                .collect(Collectors.toList());
    }

    public List<UserInfo> getAllAdmins() {
        return getUsersByRole("ADMIN_ROLES");
    }

    public List<UserInfo> getAllManagers() {
        return getUsersByRole("ADMIN_ROLES");
    }

    public List<UserInfo> getAllUser() {
        return userInfoRepository.findAll();
    }

    public UserInfo getUser(Integer id) {
        return userInfoRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    public UserInfo updateUser(Integer id, UserInfo userInfo) {
        // Récupérer l'utilisateur existant
        UserInfo existingUser = userInfoRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        // Mettre à jour les champs nécessaires (en gardant les rôles inchangés ici)
        existingUser.setName(userInfo.getName());
        existingUser.setSecondName(userInfo.getSecondName());
        existingUser.setEmail(userInfo.getEmail());
        existingUser.setNumero(userInfo.getNumero());
        existingUser.setAdresse(userInfo.getAdresse());
        existingUser.setFichier(userInfo.getFichier());
        // Encoder et mettre à jour le mot de passe seulement s’il a changé
        if (!passwordEncoder.matches(userInfo.getPassword(), existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        }

        // ⚠️ Ne pas toucher à existingUser.setRoles(...) ici !
        // Si vous voulez mettre à jour les rôles, utilisez updateUserRoles séparément

        return userInfoRepository.save(existingUser);
    }

    public UserInfo updateUserRoles(Integer id, Collection<Role> newRoles) {
        // Récupérer l'utilisateur par son ID
        UserInfo userInfo = userInfoRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        // Vérifier si les nouveaux rôles existent
        Collection<Role> validatedRoles = new ArrayList<>();
        for (Role role : newRoles) {
            Optional<Role> existingRole = roleRepository.findByNomRole(role.getNomRole());
            if (existingRole.isPresent()) {
                validatedRoles.add(existingRole.get());
            } else {
                throw new IllegalArgumentException("Role not found: " + role.getNomRole());
            }
        }

        // Mettre à jour les rôles de l'utilisateur
        userInfo.setRoles(validatedRoles);

        // Enregistrer les modifications

        return userInfoRepository.save(userInfo);
    }

    public void deleteUser(Integer id) {
        if (!userInfoRepository.existsById(id)) {
            throw new UsernameNotFoundException("User not found with id: " + id);
        }
        userInfoRepository.deleteById(id);
    }

    // Méthode pour obtenir tous les détails de l'utilisateur y compris les rôles
    public UserInfoDetails getUserDetails(String username) {
        Optional<UserInfo> userInfoOptional = userInfoRepository.findByName(username);
        UserInfo userInfo = userInfoOptional
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));
        return new UserInfoDetails(userInfo);
    }

    // public void createPasswordResetTokenForUser(UserInfo userInfo, String token)
    // {
    // PasswordResetToken myToken = new PasswordResetToken(token, userInfo, new
    // Date(System.currentTimeMillis() + 3600000)); // 1 heure
    // passwordResetTokenRepository.save(myToken);
    // }

    public void sendPasswordResetEmail(UserInfo userInfo, String token) {
        String subject = "Réinitialisation de mot de passe";
        String resetUrl = "https://www.mainsdorplus.com/reset-password?token=" + token;
        String message = "Cliquez sur le lien suivant pour réinitialiser votre mot de passe : " + resetUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(userInfo.getEmail());
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }
}
