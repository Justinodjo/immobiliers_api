package com.immoservice.immobiliers.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.immoservice.immobiliers.dto.UserInfoBadges;
import com.immoservice.immobiliers.dto.UserInfoDTO;
import com.immoservice.immobiliers.entities.AuthRequest;
import com.immoservice.immobiliers.entities.Contrats;
import com.immoservice.immobiliers.entities.PasswordResetToken;
import com.immoservice.immobiliers.entities.Permission;
import com.immoservice.immobiliers.entities.Role;
import com.immoservice.immobiliers.entities.StatusContrat;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.exceptions.RoleNotFoundException;
import com.immoservice.immobiliers.exceptions.UserAlreadyExistsException;
import com.immoservice.immobiliers.repository.BiensImmobiliersRepository;
import com.immoservice.immobiliers.repository.ContratsRepository;
import com.immoservice.immobiliers.repository.PasswordResetTokenRepository;
import com.immoservice.immobiliers.repository.RoleRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;
import com.immoservice.immobiliers.response.AuthResponse;

import jakarta.validation.Valid;

@Service
public class UserInfoService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserInfoService.class);
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ContratsService contratsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private BiensImmobiliersRepository biensImmobiliersRepository;

    @Autowired
    private ContratsRepository contratsRepository;

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

    // Utilisation de DTO et validation
    public UserInfo addUser(@Valid UserInfoDTO userInfoDTO) {
        if (userInfoRepository.findByEmail(userInfoDTO.getEmail()).isPresent()) {
            logger.warn("Tentative de création d'un utilisateur déjà existant : {}", userInfoDTO.getEmail());
            throw new UserAlreadyExistsException(
                    "Un utilisateur avec cet email existe déjà : " + userInfoDTO.getEmail());
        }
        if (userInfoRepository.findByName(userInfoDTO.getName()).isPresent()) {
            logger.warn("Tentative de création d'un utilisateur déjà existant (nom) : {}", userInfoDTO.getName());
            throw new UserAlreadyExistsException("Un utilisateur avec ce nom existe déjà : " + userInfoDTO.getName());
        }

        // Mapping DTO -> Entity (à adapter selon ton DTO)
        UserInfo userInfo = new UserInfo();
        userInfo.setName(userInfoDTO.getName());
        userInfo.setSecondName(userInfoDTO.getSecondName());
        userInfo.setEmail(userInfoDTO.getEmail());
        userInfo.setNumero(userInfoDTO.getNumero());
        userInfo.setAdresse(userInfoDTO.getAdresse());
        userInfo.setDateNaissance(userInfoDTO.getDateNaissance());
        userInfo.setPassword(passwordEncoder.encode(userInfoDTO.getPassword()));

        // Gestion des rôles
        Collection<Role> validatedRoles = new ArrayList<>();
        if (userInfoDTO.getRoles() != null && !userInfoDTO.getRoles().isEmpty()) {
            for (String roleName : userInfoDTO.getRoles()) {
                Optional<Role> existingRole = roleRepository.findByNomRole(roleName);
                if (existingRole.isPresent()) {
                    validatedRoles.add(existingRole.get());
                } else {
                    logger.error("Rôle non trouvé : {}", roleName);
                    throw new RoleNotFoundException("Rôle non trouvé : " + roleName);
                }
            }
        } else {
            Optional<Role> defaultRole = roleRepository.findByNomRole("CLIENTS_ROLES");
            if (defaultRole.isPresent()) {
                validatedRoles.add(defaultRole.get());
            } else {
                throw new RoleNotFoundException("Rôle par défaut non trouvé : CLIENTS_ROLES");
            }
        }
        userInfo.setRoles(validatedRoles);

        UserInfo savedUserInfo = userInfoRepository.save(userInfo);
        logger.info("Utilisateur créé : {}", savedUserInfo.getEmail());
        // sendWelcomeEmailWithReset(savedUserInfo);
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
                    .flatMap(role -> {
                        // Autorité du rôle
                        var roleAuthority = new SimpleGrantedAuthority("ROLE_" + role.getNomRole());

                        // Autorités des permissions
                        var permissionAuthorities = role.getPermissions().stream()
                                .map(permission -> new SimpleGrantedAuthority(permission.getNom()));

                        // Fusion rôle + permissions
                        return Stream.concat(Stream.of(roleAuthority), permissionAuthorities);
                    })
                    .collect(Collectors.toSet());

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

            List<String> permissions = user.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(Permission::getNom)
                    .distinct()
                    .toList();
            // Générer le token JWT
            String token = jwtService.generateToken(user.getName(), user.getEmail(), authorities);

            // Retourner la réponse d'authentification avec le token et les informations
            // utilisateur
            return new AuthResponse(user.getId(), token, user.getName(), user.getSecondName(), user.getNumero(),
                    user.getEmail(), fichierPrincipal, fichiers, authorities, permissions);
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
        return getUsersByRole("MANAGER_ROLES");
    }

    public List<UserInfo> getAllUser() {
        return userInfoRepository.findAll();
    }

    // Gestion réelle des tokens de reset password
    public void createPasswordResetTokenForUser(UserInfo userInfo, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, userInfo,
                new Date(System.currentTimeMillis() + 3600000));
        passwordResetTokenRepository.save(myToken);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        UserInfo userInfo = passwordResetToken.getUserInfo();
        userInfo.setPassword(passwordEncoder.encode(newPassword));
        userInfoRepository.save(userInfo);
        passwordResetTokenRepository.delete(passwordResetToken);
    }

    public UserInfo getUser(Integer id) {
        return userInfoRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    public UserInfo updateUser(Integer id, @Valid UserInfoDTO userInfoDTO) {
        UserInfo existingUser = userInfoRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        existingUser.setName(userInfoDTO.getName());
        existingUser.setSecondName(userInfoDTO.getSecondName());
        existingUser.setEmail(userInfoDTO.getEmail());
        existingUser.setNumero(userInfoDTO.getNumero());
        existingUser.setAdresse(userInfoDTO.getAdresse());
        existingUser.setDateNaissance(userInfoDTO.getDateNaissance());
        if (!passwordEncoder.matches(userInfoDTO.getPassword(), existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(userInfoDTO.getPassword()));
        }
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

    public int getUserScore(Integer userId) {
        UserInfo user = userInfoRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

        return contratsService.calculerScoreUtilisateur(user);
    }

    public List<UserInfo> proposerGestionnairesPourProprietaire(Integer id, int scoreMin) {
        UserInfo proprietaire = userInfoRepository.findById(
                id)
                .orElseThrow(() -> new UsernameNotFoundException("Propriétaire non trouvé"));

        String adresseProprietaire = proprietaire.getAdresse();

        return getUsersByRole("MANAGER_ROLES").stream()
                .filter(g -> g.getAdresse() != null && g.getAdresse().equalsIgnoreCase(adresseProprietaire)) // filtre
                // par zone
                .filter(g -> getUserScore(g.getId()) >= scoreMin) // filtre par score
                .collect(Collectors.toList());
    }

    public List<UserInfo> proposerAdministrateursPourGestionnaire(Integer id, int scoreMin) {
        UserInfo gestionnaire = userInfoRepository.findById(
                id)
                .orElseThrow(() -> new UsernameNotFoundException("Gestionnaire non trouvé"));

        String adresseGestionnaire = gestionnaire.getAdresse();

        return getUsersByRole("ADMIN_ROLES").stream()
                .filter(admin -> admin.getAdresse() != null && admin.getAdresse().equalsIgnoreCase(adresseGestionnaire))
                .filter(admin -> getUserScore(admin.getId()) >= scoreMin)
                .collect(Collectors.toList());
    }

    public UserInfoBadges genererBadgesPourUtilisateur(UserInfo userInfo) {
        // Vérifie si l'utilisateur a le rôle "PROPRIETAIRE_ROLES"
        boolean isProprietaire = userInfo.getRoles().stream()
                .anyMatch(role -> "PROPRIETAIRE_ROLES".equalsIgnoreCase(role.getNomRole()));

        // Badge propriétaire vérifié seulement si c'est un propriétaire et qu'il
        // possède au moins un bien
        boolean proprietaireVerifie = isProprietaire && biensImmobiliersRepository.countByUserInfo(userInfo) > 0;

        // Récupération des contrats liés à l’utilisateur
        List<Contrats> contrats = contratsRepository.findByUserInfo(userInfo);

        // ✅ Badge locataire sérieux : tous les contrats actifs et aucun frais
        // d’annulation
        boolean locataireSerieux = !contrats.isEmpty()
                && contrats.stream().allMatch(c -> c.getStatus() == StatusContrat.ACTIF)
                && contrats.stream().noneMatch(c -> c.getFraisAnnulation() != null && c.getFraisAnnulation() > 0);

        return new UserInfoBadges(
                userInfo.getId(),
                userInfo.getName() + " " + userInfo.getSecondName(),
                proprietaireVerifie,
                locataireSerieux);
    }

}
