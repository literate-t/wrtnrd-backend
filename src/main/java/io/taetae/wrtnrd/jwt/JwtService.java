package io.taetae.wrtnrd.jwt;

import static io.jsonwebtoken.Jwts.builder;
import static io.jsonwebtoken.Jwts.parserBuilder;
import static java.lang.System.currentTimeMillis;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtService {

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.access-token.expiry-time}")
  private Long accessTokenExpiration;

  @Value("${jwt.refresh-token.expiry-time}")
  private Long refreshTokenExpiration;

  public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {

    final Claims claims = extractAllClaims(token);
    claimResolver.apply(claims);

    return claimResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {

    return parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJwt(token)
        .getBody();
  }

  public String extractUsername(String token) {

    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {

    return extractClaim(token, Claims::getExpiration);
  }

  public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {

    return buildToken(extraClaims, userDetails, accessTokenExpiration);
  }

  public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {

    return buildToken(extraClaims, userDetails, refreshTokenExpiration);
  }

  private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiryTime) {

    return builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(currentTimeMillis()))
        .setExpiration(new Date(currentTimeMillis() + expiryTime))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {

    final String extractedUsername = extractUsername(token);

    return extractedUsername.equals(userDetails.getUsername());
  }

  private Key getSignInKey() {

    return Keys.hmacShaKeyFor(secretKey.getBytes());
  }
}
