package org.serratec.projetofinal_api_g4.config;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    public String generateToken(Authentication authentication) {
        String principal = authentication.getName(); // id:nome:email
        String[] parts = principal.split(":");
        String id = parts[0];
        String nome = parts[1];
        String email = parts[2];

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                .orElse("USER");

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("nome", nome);
        claims.put("email", email);

        // Definindo authorities para o Spring Security
        List<String> authorities = new ArrayList<>();

        if ("CLIENTE".equalsIgnoreCase(role)) {
            authorities.add("ROLE_CLIENTE");
        } else {
            // Se for um funcionário (pode ser ADMIN, GERENTE, etc.)
            authorities.add("ROLE_" + role.toUpperCase()); // ex: ROLE_ADMIN, ROLE_GERENTE
        }

        claims.put("authorities", authorities);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(principal) // Guarda o username completo com id:nome:email
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        // Retorna o "subject" completo: id:nome:email
        return getAllClaimsFromToken(token).getSubject();
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            logger.error("JWT token is null or empty");
            return false;
        }
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}