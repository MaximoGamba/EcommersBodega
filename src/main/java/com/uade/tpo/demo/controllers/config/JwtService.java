package com.uade.tpo.demo.controllers.config;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import com.uade.tpo.demo.entity.User;

@Service
public class JwtService { 
    @Value("${application.security.jwt.secretKey}") 
    private String secretKey; 
    @Value("${application.security.jwt.expiration}") 
    private long jwtExpiration; 

    public String generateToken( 
            UserDetails userDetails) { 
        Map<String, Object> extraClaims = new HashMap<>();
        if (userDetails instanceof User user) {
            extraClaims.put("tokenVersion", user.getTokenVersion() == null ? 0 : user.getTokenVersion());
        }
        return buildToken(extraClaims, userDetails, jwtExpiration); 
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) { 
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername()) 
                .issuedAt(new Date(System.currentTimeMillis())) 
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey()) 
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) { 
        final String username = extractClaim(token, Claims::getSubject); 
        Integer tokenVersionFromToken = extractTokenVersion(token);
        int tokenVersionFromUser = 0;
        if (userDetails instanceof User user && user.getTokenVersion() != null) {
            tokenVersionFromUser = user.getTokenVersion();
        }
        return (username.equals(userDetails.getUsername()))
                && !isTokenExpired(token)
                && tokenVersionFromToken != null
                && tokenVersionFromToken == tokenVersionFromUser; // Invalida tokens viejos cuando hay una nueva versión
    }

    private Integer extractTokenVersion(String token) {
        try {
            Object value = extractAllClaims(token).get("tokenVersion");
            if (value instanceof Integer intValue) {
                return intValue;
            }
            if (value instanceof Number number) {
                return number.intValue();
            }
            if (value instanceof String str) {
                return Integer.parseInt(str);
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    private boolean isTokenExpired(String token) { // Verifica si el token ha expirado
        return extractClaim(token, Claims::getExpiration).before(new Date()); // Verifica si el token ha expirado
    }

    public String extractUsername(String token) { // Extrae el username del token
        return extractClaim(token, Claims::getSubject); // Extrae el username del token
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { // Extrae el claim del token
        final Claims claims = extractAllClaims(token); // Extrae los claims del token
        return claimsResolver.apply(claims); // Aplica la función de claimsResolver a los claims
    }

    private Claims extractAllClaims(String token) { // Extrae los claims del token
        return Jwts // Crea un builder para el token
                .parser() // Crea un parser para el token
                .verifyWith(getSecretKey()) // Verifica el token con la clave secreta
                .build()
                .parseSignedClaims(token) // Parsea el token y devuelve los claims
                .getPayload();
    }

    private SecretKey getSecretKey() { // Obtiene la clave secreta
        SecretKey secretKeySpec = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)); // Crea una clave
                                                                                                  // secreta para el
                                                                                                  // token
        return secretKeySpec; // Devuelve la clave secreta
    }
}
