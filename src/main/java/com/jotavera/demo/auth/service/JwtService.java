package com.jotavera.demo.auth.service;

import com.jotavera.demo.auth.exception.JwtInvalidException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Service for generating and validating JWT tokens.
 */
@Service
public class JwtService {

    private final Key key;

    /**
     * Initializes the JWT signing key.
     * TODO must be a secret
     */
    public JwtService() {
        String secret = "my-super-secret-key-should-be-32-bytes!";
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a JWT token for the given subject.
     *
     * @param subject the token subject (e.g., user email)
     * @return the signed JWT token
     */
    public String generateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the subject (e.g., email) from a JWT token.
     *
     * @param token the JWT token
     * @return the subject
     * @throws JwtInvalidException if the token is invalid or expired
     */
    public String extractSubject(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException ex) {
            throw new JwtInvalidException("Invalid or expired JWT token", ex);
        }
    }

    /**
     * Checks if a JWT token is valid and not expired.
     *
     * @param token the JWT token
     * @return true if valid, false otherwise
     */
    public boolean isValid(String token) {
        try {
            return !Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (JwtException e) {
            return false;
        }
    }
}