package com.uade.tpo.demo.controllers.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.security.StandardSecureDigestAlgorithms;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecretKeyBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

@Service
public class JwtService {
    @Value("${application.security.jwt.secretKey}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(
            UserDetails userDetails) {
        return buildToken(userDetails, jwtExpiration);
    }

    private String buildToken(
            UserDetails userDetails,
            long expiration) {
        return Jwts
                .builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractClaim(token, Claims::getSubject); // Extrae el username del token
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token); // Compara el username del token
                                                                                       // con el username del usuario
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date()); // Verifica si el token ha expirado
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // Extrae el username del token
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // Extrae los claims del token
        return claimsResolver.apply(claims); // Aplica la función de claimsResolver a los claims
    }

    private Claims extractAllClaims(String token) {
        return Jwts // Crea un builder para el token
                .parser() // Crea un parser para el token
                .verifyWith(getSecretKey()) // Verifica el token con la clave secreta
                .build()
                .parseSignedClaims(token) // Parsea el token y devuelve los claims
                .getPayload();
    }

    private SecretKey getSecretKey() {
        SecretKey secretKeySpec = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)); // Crea una clave
                                                                                                  // secreta para el
                                                                                                  // token
        return secretKeySpec; // Devuelve la clave secreta
    }
}
