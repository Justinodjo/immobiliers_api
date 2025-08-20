package com.immoservice.immobiliers.config;

import java.io.Serializable;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.immoservice.immobiliers.entities.Permission;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.PermissionRepository;
import org.springframework.security.core.userdetails.User;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private PermissionRepository permissionRepository; // Récupérer les permissions depuis la DB

    /**
     * Vérifie si l'utilisateur a la permission sur un objet donné
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || permission == null) {
            return false;
        }

        // Récupérer l'utilisateur connecté
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        if (userInfo == null) {
            return false;
        }

        // Récupérer les permissions de l'utilisateur depuis la base de données
        Set<Permission> userPermissions = userInfo.getPermissions();

        // Vérifier si l'utilisateur possède la permission demandée
        return userPermissions.stream()
                .anyMatch(p -> p.getNom().equals(permission.toString()));
    }

    /**
     * Permet de gérer les permissions par ID et type d'entité
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission) {
        if (authentication == null || permission == null || targetId == null || targetType == null) {
            return false;
        }

        // Récupérer l'utilisateur connecté
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        if (userInfo == null) {
            return false;
        }

        // Logique ici pour vérifier les permissions spécifiques à un ID et type
        // d'entité
        // Exemple : vérifier si l'utilisateur a la permission sur un objet avec un ID
        // spécifique
        Set<Permission> userPermissions = userInfo.getPermissions();
        // On pourrait envisager de rechercher dans la DB si l'ID et le type correspond
        // à une entité avec la permission
        return userPermissions.stream()
                .anyMatch(p -> p.getNom().equals(permission.toString()));
    }
}
