package org.serratec.projetofinal_api_g4.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

  @Value("${auth.jwt-secret}")
  private String jwtSecret;

  @Value("${auth.jwt-expiration-milisegundos}")
  private Long jwtExpirationMiliseg;

  // Implementação para gerar o token JWT usando o secret e expiration
  public String generateToken(String username) {
     SecretKey secretKeySpec = Keys.hmacShaKeyFor(jwtSecret.getBytes());
     return Jwts.builder()
              .setSubject(username)
              .setExpiration(new Date(System.currentTimeMillis() + this.jwtExpirationMiliseg))
              .signWith(secretKeySpec)
              .compact();
  }

  public boolean isValidToken(String token){
    Claims claims = getClaims(token);
    if(claims != null) {
      String username = claims.getSubject();
      Date expirationDate = claims.getExpiration();
      Date now = new Date(System.currentTimeMillis());
      if(username != null && now.before(expirationDate)) {
        return true;
      }
    }
    return false;
  }

  public String getUserName(String token) {
    Claims claims = getClaims(token);
    if(claims != null) {
      return claims.getSubject();
    }
    return null;
  }

  public Claims getClaims(String token){
    return Jwts.parserBuilder()
          .setSigningKey(jwtSecret.getBytes())
          .build()
          .parseClaimsJws(token)
          .getBody();
  }
}
