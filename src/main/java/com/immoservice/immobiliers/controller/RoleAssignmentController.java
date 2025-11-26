package com.immoservice.immobiliers.controller;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.immoservice.immobiliers.services.JwtService;
import com.immoservice.immobiliers.entities.Role;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.RoleRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class RoleAssignmentController {

    @Autowired
    private UserInfoRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/assign-role")
    public ResponseEntity<?> assignRoleToUser(@RequestParam String email, @RequestParam String roleName) {
        Optional<UserInfo> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Utilisateur introuvable");
        }

        Optional<Role> optionalRole = roleRepository.findByNomRole(roleName);
        if (optionalRole.isEmpty()) {
            return ResponseEntity.badRequest().body("Rôle invalide");
        }

        UserInfo user = optionalUser.get();
        Role role = optionalRole.get();

        user.getRoles().clear(); // facultatif : si tu veux forcer un seul rôle
        user.getRoles().add(role);
        userRepository.save(user);

        return ResponseEntity.ok("Rôle '" + roleName + "' attribué avec succès à " + email);
    }

    @PostMapping("/finalize-oauth")
public ResponseEntity<?> finalizeOAuth(@RequestParam String email) {
    UserInfo user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

    Collection<GrantedAuthority> authorities = user.getRoles().stream()
        .flatMap(role -> Stream.concat(
            Stream.of(new SimpleGrantedAuthority("ROLE_" + role.getNomRole())),
            role.getPermissions().stream().map(p -> new SimpleGrantedAuthority(p.getNom()))
        ))
        .collect(Collectors.toSet());

    String token = jwtService.generateToken(user.getName(), user.getEmail(), authorities);

    return ResponseEntity.ok(Map.of("token", token, "roles", authorities.stream()
        .map(GrantedAuthority::getAuthority).collect(Collectors.toList())));
}

}
