package com.immoservice.immobiliers.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.immoservice.immobiliers.entities.CustomOAuth2User;
import com.immoservice.immobiliers.entities.Fichier;
import com.immoservice.immobiliers.entities.Role;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.RoleRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);

        String provider = request.getClientRegistration().getRegistrationId().toUpperCase();
        String providerId;

        if ("GOOGLE".equals(provider)) {
            providerId = oAuth2User.getAttribute("sub");
        } else if ("FACEBOOK".equals(provider)) {
            providerId = oAuth2User.getAttribute("id");
        } else {
            throw new IllegalArgumentException("Provider non supporté: " + provider);
        }
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("given_name");
        String secondName = oAuth2User.getAttribute("family_name");
        String picture = oAuth2User.getAttribute("picture");

        UserInfo user = userInfoRepository.findByEmail(email).orElseGet(() -> {
            // Nouvel utilisateur
            UserInfo newUser = new UserInfo();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setSecondName(secondName);
            newUser.setProvider(provider);
            newUser.setProviderId(providerId);

            // Récupérer rôle choisi depuis l'URL ou paramètre (à passer depuis le frontend)
            String selectedRole = getSelectedRoleFromRequest(request); // méthode custom
            if (selectedRole != null && !selectedRole.isEmpty()) {
                Role role = roleRepository.findByNomRole(selectedRole)
                        .orElseThrow(() -> new RuntimeException("Rôle introuvable"));
                newUser.getRoles().add(role);
            } else {
                // Optionnel : rôle par défaut si rien choisi
                // Role defaultRole = roleRepository.findByNomRole("LOCATAIRE_ROLES")
                // .orElseThrow(() -> new RuntimeException("Rôle par défaut introuvable"));
                // newUser.getRoles().add(defaultRole);
            }
            return newUser;
        });

        // Mise à jour si nécessaire
        user.setProvider(provider);
        user.setProviderId(providerId);
        // ✅ Créer un objet Fichier pour l'image OAuth
        if (picture != null && !picture.isEmpty()) {
            Fichier pictureFile = new Fichier();
            pictureFile.setUrl(picture); // Assumer que Fichier a un champ 'url'
            pictureFile.setPrincipal(true); // Définit comme fichier principal
            pictureFile.setUserInfo(user); // Lien vers l'utilisateur
            user.setFichierPrincipal(pictureFile); // Méthode utilitaire pour définir le principal
        }

        userInfoRepository.save(user);

        return new CustomOAuth2User(user, oAuth2User);
    }

    private String getSelectedRoleFromRequest(OAuth2UserRequest request) {
        // Récupère l'URL de redirection configurée
        String redirectUri = request.getAdditionalParameters().get("redirect_uri") != null
                ? request.getAdditionalParameters().get("redirect_uri").toString()
                : null;

        if (redirectUri == null)
            return null;

        try {
            java.net.URI uri = new java.net.URI(redirectUri);
            String query = uri.getQuery(); // exemple: email=john@example.com&selectedRole=LOCATAIRE_ROLES
            if (query == null)
                return null;

            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "selectedRole".equals(keyValue[0])) {
                    return java.net.URLDecoder.decode(keyValue[1], java.nio.charset.StandardCharsets.UTF_8);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Aucun rôle choisi
    }

}
