package com.example.project.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * Service for handling JSON Web Token (JWT) generation, extraction, and validation.
 * Utilizes security properties defined in application configuration.
 */
@Service
public class JwtService {

  @Value("${application.security.jwt.secret-key}")
  private String secretKey;

  @Value("${application.security.jwt.expiration}")
  private long jwtExpiration;

  /**
   * Extracts the username (subject) from the provided JWT token.
   *
   * @param token the JWT token
   * @return the extracted username
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extracts a specific claim from the JWT token using a resolver function.
   *
   * @param token          the JWT token
   * @param claimsResolver the function to resolve the claim
   * @param <T>            the type of the claim
   * @return the resolved claim value
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Generates a new JWT token for a specific user.
   *
   * @param username the username to include in the token
   * @return a signed and compacted JWT string
   */
  public String generateToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Validates if the token belongs to the user and has not expired.
   *
   * @param token    the JWT token
   * @param username the expected username
   * @return true if valid, false otherwise
   */
  public boolean isTokenValid(String token, String username) {
    final String extractedUsername = extractUsername(token);
    return (extractedUsername.equals(username)) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}