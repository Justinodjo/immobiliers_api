package com.immoservice.immobiliers.services;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immoservice.immobiliers.logout.BlackList;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {
    @Autowired
    private BlackList blackList;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String SECRET = "!@#$FDGSDFGSGSGSGSHSHSHSSHGFFDSGSFGSSGHSDFSDFSFSFSFSDFSFSFSFAADDCCEEBBKKEERRDDEEFFFDDCGGFDDDDFGG";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;
    private final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7;
    // ===========================
    // 🔹 Génération du token JWT
    // ===========================
    // Méthode pour encoder le secret en Base64

    public String generateToken(String userName, String email, Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();

        List<String> roles = authorities.stream()
                .filter(a -> a.getAuthority().startsWith("ROLE_"))
                .map(GrantedAuthority::getAuthority)
                .toList();

        List<String> permissions = authorities.stream()
                .filter(a -> !a.getAuthority().startsWith("ROLE_"))
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Ajoutez les rôles de l'utilisateur dans les réclamations (claims)
        claims.put("roles", roles);
        claims.put("permissions", permissions); // Ajoutez les permissions aux réclamations
        // Log pour vérifier
        claims.put("email", email); // Ajoutez l'email aux réclamations
        claims.put("name", userName);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 30 minutes expiration
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String username, String email,
            Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();

        List<String> roles = authorities.stream()
                .filter(a -> a.getAuthority().startsWith("ROLE_"))
                .map(GrantedAuthority::getAuthority)
                .toList();

        List<String> permissions = authorities.stream()
                .filter(a -> !a.getAuthority().startsWith("ROLE_"))
                .map(GrantedAuthority::getAuthority)
                .toList();

        claims.put("roles", roles);
        claims.put("permissions", permissions);
        claims.put("email", email);
        claims.put("name", username);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // utilise getSignKey()
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = SECRET.getBytes(); // pas besoin de Base64 ici
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ===========================
    // 🔹 Extraction des claims
    // ===========================
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class)); // Nouvelle méthode pour extraire
                                                                                 // l'email
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ===========================
    // 🔹 Validation du token
    // ===========================
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String emailFromToken = extractUserName(token);
            final String subject = extractUserName(token);
            if (emailFromToken == null || userDetails == null) {
                System.out.println("⚠️ Email ou UserDetails null pendant la validation.");
                return false;
            }
            boolean match = emailFromToken.equalsIgnoreCase(userDetails.getUsername())
                    || (subject != null && subject.equalsIgnoreCase(userDetails.getUsername()));

            boolean valid = match && !isTokenExpired(token) && !blackList.isBlackListed(token);
            if (!valid) {
                System.out.println("⚠️ Token invalide pour l'utilisateur : " + userDetails.getUsername());
            }
            return valid;
        } catch (ExpiredJwtException e) {
            // Log ou gérer l'exception
            System.out.println("Token expiré : " + e.getMessage());
            return false; // Renvoie false si le token est expiré
        } catch (Exception e) {
            System.out.println("❌ Erreur pendant la validation du token : " + e.getMessage());
            return false;
        }
    }

    // Nouvelle méthode pour valider le token avec un seul argument
    public Boolean validateToken(String token) {
        return !isTokenExpired(token) && !blackList.isBlackListed(token);
    }

    // ===========================
    // 🔹 Rôles et permissions
    // ===========================
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Object rolesObject = extractAllClaims(token).get("roles");
        if (rolesObject == null)
            return List.of();
        return objectMapper.convertValue(rolesObject, new TypeReference<List<String>>() {
        });
    }

    @SuppressWarnings("unchecked")
    public List<String> extractPermissions(String token) {
        Object permissionsObject = extractAllClaims(token).get("permissions");
        if (permissionsObject == null)
            return List.of();
        return objectMapper.convertValue(permissionsObject, new TypeReference<List<String>>() {
        });
    }

}
