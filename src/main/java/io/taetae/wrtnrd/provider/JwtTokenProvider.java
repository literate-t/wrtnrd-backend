package io.taetae.wrtnrd.provider;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.taetae.wrtnrd.domain.model.PrincipalUser;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  @Value("${jwt.header}")
  private String jwtSecretKey;

  @Value("${jwt.access-token-validity-time}")
  private Long accessTokenValidityTime;

  @Value("${jwt.refresh-token-validity-time}")
  private Long refreshTokenValidityTime;

  public String generateAccessToken(Authentication authentication) {

    PrincipalUser principal = getPrincipal(authentication);
    Date expiryTime = getExpiryDate(accessTokenValidityTime);

    return Jwts.builder()
        .setSubject(principal.getUsername())
        .claim("id", principal.getId())
        .claim("username", principal.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(expiryTime)
        .signWith(getSecretKey())
        .compact();
  }

  public String generateRefreshToken(Authentication authentication) {

    PrincipalUser principal = getPrincipal(authentication);
    Date expiryTime = getExpiryDate(refreshTokenValidityTime);

    return Jwts.builder()
        .setSubject(principal.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(expiryTime)
        .signWith(getSecretKey())
        .compact();
  }

//  private String generateToken(PrincipalUser principal, Date expiryTime) {
//    return Jwts.builder()
//        .setSubject(principal.getUsername())
//        .claim("id", principal.getId())
//        .claim("username", principal.getUsername())
//        .setIssuedAt(new Date())
//        .setExpiration(expiryTime)
//        .signWith(getSecretKey())
//        .compact();
//  }

  private Date getExpiryDate(Long expiryDate) {
    return new Date(new Date().getTime() + expiryDate);
  }

  private PrincipalUser getPrincipal(Authentication authentication) {
    return (PrincipalUser) authentication.getPrincipal();
  }

  private Key getSecretKey() {
    return Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
  }

  public Long getUserIdFromToken(String token) {

    return (Long) Jwts.parserBuilder()
        .setSigningKey(getSecretKey())
        .requireId("id")
        .build()
        .parse(token)
        .getBody();
  }

  public String getUsernameFromToken(String token) {

    return (String) Jwts.parserBuilder()
        .setSigningKey(getSecretKey())
        .requireId("username")
        .build()
        .parse(token)
        .getBody();
  }
}
