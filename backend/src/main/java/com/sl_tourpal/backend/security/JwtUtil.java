package com.sl_tourpal.backend.security;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

  private final SecretKey secretKey;
  private final long jwtExpirationMs;

  public JwtUtil(
      @Value("${security.jwt.secret}") String secretBase64,
      @Value("${security.jwt.expiration}") long jwtExpirationMs
  ) {
    // Create a key from the base64-encoded secret
    byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    this.jwtExpirationMs = jwtExpirationMs;
  }

  // Generate a token containing username and authorities as claims
  public String generateToken(UserDetails userDetails) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .claim("authorities", userDetails.getAuthorities())
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  // Extract username (subject) from token
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  // Generic claim extractor
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = parseAllClaims(token);
    return claimsResolver.apply(claims);
  }

  // Validate token expiry and signature
  public boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims parseAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
// This class handles JWT token generation, validation, and claim extraction.
// It uses a secret key for signing and verifying tokens, and provides methods to extract user information and validate token integrity.