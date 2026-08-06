package com.immoservice.immobiliers.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.immoservice.immobiliers.dto.TokenResponse;
import com.immoservice.immobiliers.dto.UserInfoDTO;
import com.immoservice.immobiliers.entities.AuthRequest;
import com.immoservice.immobiliers.entities.Role;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.logout.BlackList;
import com.immoservice.immobiliers.response.AuthResponse;
import com.immoservice.immobiliers.services.JwtService;
import com.immoservice.immobiliers.services.UserInfoDetails;
import com.immoservice.immobiliers.services.UserInfoService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

// import com.example.demo.filter.TokenUtil;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserInfoService userInfoService;
    // @Autowired
    // private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private BlackList blackList;

    // @Autowired
    // @Value("${token.secret}")
    // private String tokenSecret;

    // @Autowired
    // private TokenUtil tokenUtil; // Injection de TokenUtil

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Spring Security project to ModePlus !!";
    }

    @PostMapping("/addUser")
    public UserInfo addUser(@RequestBody @Valid UserInfoDTO userInfoDTO) {
        return userInfoService.addUser(userInfoDTO);
    }

    // @PostMapping("/login")
    // public AuthResponse login(@RequestBody AuthRequest authRequest) {
    // Authentication authenticate = authenticationManager.authenticate(
    // new UsernamePasswordAuthenticationToken(authRequest.getUserName(),
    // authRequest.getPassword()));
    // if (authenticate.isAuthenticated()) {
    // String token = jwtService.generateToken(authRequest.getUserName(),
    // authRequest.getEmail(),
    // authenticate.getAuthorities());
    // return new AuthResponse(token, authRequest.getUserName(),
    // authRequest.getEmail(),
    // authenticate.getAuthorities());
    // } else {
    // throw new UsernameNotFoundException("Invalid user request");
    // }
    // }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        return userInfoService.login(authRequest);
    }

    @PostMapping("/logout")
    // @PreAuthorize("hasAuthority('CLIENT_ROLES') or hasAuthority('ADMIN_ROLES')")
    public String logoutUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer")) {
            token = authHeader.substring(7);
        }
        blackList.blacKListToken(token);
        return "You have successfully logged out !!";
    }

    @PostMapping("/renew-token")
    public String renewToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.validateToken(token)) {
                String username = jwtService.extractUserName(token);
                String email = jwtService.extractEmail(token); // Extrait l'email du token
                UserDetails userDetails = userInfoService.loadUserByUsername(username);
                if (jwtService.validateToken(token, userDetails)) {

                    return jwtService.generateToken(username, email, userDetails.getAuthorities());
                }
            }
        }
        throw new RuntimeException("Invalid or expired token");
    }

    // @GetMapping("/getToken")
    // public ResponseEntity<String> getToken(@RequestHeader HttpHeaders header){
    // String userId = tokenUtil.getUserIdByToken(header, tokenSecret);
    // return ResponseEntity.status(HttpStatus.OK).body(userId);
    // }

    @GetMapping("/getUsers")
    // @PreAuthorize("hasAuthority('ADMIN_ROLES') or hasAuthority('USER_ROLES')")
    public List<UserInfo> getAllUsers() {
        return userInfoService.getAllUser();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Vérifie si le refresh token est valide et non blacklisté
        if (!jwtService.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Récupère l'utilisateur correspondant
        String email = jwtService.extractUserName(refreshToken);
        UserInfo user = userInfoService.findByUsername(email);

        // Construit les authorities (roles + permissions)
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .flatMap(role -> {
                    Stream<GrantedAuthority> roleAuth = Stream
                            .of(new SimpleGrantedAuthority("ROLE_" + role.getNomRole()));
                    Stream<GrantedAuthority> permAuth = role.getPermissions().stream()
                            .map(p -> new SimpleGrantedAuthority(p.getNom()));
                    return Stream.concat(roleAuth, permAuth);
                })
                .collect(Collectors.toSet());

        // Génère les nouveaux tokens
        String newAccessToken = jwtService.generateToken(user.getName(), user.getEmail(), authorities);
        String newRefreshToken = jwtService.generateRefreshToken(user.getName(), user.getEmail(), authorities);

        // Optionnel : blacklist le refresh token utilisé pour éviter le réutiliser
        blackList.blacKListToken(refreshToken);

        // Renvoie les tokens dans le DTO
        TokenResponse tokenResponse = new TokenResponse(newAccessToken, newRefreshToken);
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        // Lire le token depuis le cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    String jwt = cookie.getValue();
                    // Vérifier le JWT et récupérer l'utilisateur
                    UserInfo user = userInfoService.getUserFromToken(jwt);
                    return ResponseEntity.ok(user);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Aucun utilisateur connecté via OAuth2");
    }

    @GetMapping("/getAdmins")
    @PreAuthorize("hasAuthority('ADMIN_ROLES')")
    public List<UserInfo> getAllAdmins() {
        return userInfoService.getAllAdmins();
    }

    @GetMapping("/getManagers")
    @PreAuthorize("hasAuthority('ADMIN_ROLES')")
    public List<UserInfo> getAllManagers() {
        return userInfoService.getAllManagers();
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAuthority('VIEW_USER')")
    public UserInfoDetails getUserDetails(@PathVariable String username) {
        return userInfoService.getUserDetails(username);
    }

    @GetMapping("/getUsers/{id}")
    // @PreAuthorize("hasAuthority('VIEW_USER')")
    public UserInfo getAllUsers(@PathVariable Integer id) {
        return userInfoService.getUser(id);
    }

    @PutMapping("/updateUser/{id}")
    public UserInfo updateUser(@PathVariable Integer id, @RequestBody @Valid UserInfoDTO userInfoDTO) {
        return userInfoService.updateUser(id, userInfoDTO);
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public UserInfo updateUserRoles(@PathVariable Integer id, @RequestBody Collection<Role> newRoles) {
        return userInfoService.updateUserRoles(id, newRoles);
    }

    @DeleteMapping("/deleteUser/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userInfoService.deleteUser(id);
        // "User with ID " + id + " has been deleted";
    }

    @GetMapping("/gestionnaires/{id}")
    public List<UserInfo> suggGestionnaires(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "50") int scoreMin) {
        return userInfoService.proposerGestionnairesPourProprietaire(id, scoreMin);
    }

    @GetMapping("/administrateurs/{id}")
    public List<UserInfo> suggAdmins(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "50") int scoreMin) {
        return userInfoService.proposerAdministrateursPourGestionnaire(id, scoreMin);
    }

    /**
     * Récupérer tous les locataires
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserInfo>> getAllLocataires() {
        try {
            List<UserInfo> locataires = userInfoService.getUsersByRole("LOCATAIRE_ROLES");
            return ResponseEntity.ok(locataires);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer un locataire par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserInfo> getLocataireById(@PathVariable Integer id) {
        try {
            UserInfo locataire = userInfoService.getUser(id);
            return ResponseEntity.ok(locataire);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Récupérer tous les gestionnaires
     */
    @GetMapping("/gestionnaires")
    public ResponseEntity<List<UserInfo>> getAllGestionnaires() {
        try {
            List<UserInfo> gestionnaires = userInfoService.getUsersByRole("MANAGER_ROLES");
            return ResponseEntity.ok(gestionnaires);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
