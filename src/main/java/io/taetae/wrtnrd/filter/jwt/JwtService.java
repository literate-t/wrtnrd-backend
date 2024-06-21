package io.taetae.wrtnrd.filter.jwt;

import static io.jsonwebtoken.Jwts.builder;
import static io.jsonwebtoken.Jwts.parserBuilder;
import static io.taetae.wrtnrd.util.Constant.REFRESH_TOKEN;
import static io.taetae.wrtnrd.util.Constant.TOKEN_TYPE;
import static java.lang.System.currentTimeMillis;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.taetae.wrtnrd.repository.TokenRepository;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtService {

  private final UserDetailsService customUserDetailsService;
  private final TokenRepository tokenRepository;

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.access-token.expiration}")
  private Long accessTokenExpiration;

  @Value("${jwt.refresh-token.expiration}")
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
        .parseClaimsJws(token)
        .getBody();
  }

  private Claims extractAllClaimsForException(String token) {

    try {
      return parserBuilder()
          .setSigningKey(getSignInKey())
          .build()
          .parseClaimsJws(token)
          .getBody();
    } catch(ExpiredJwtException e) {
      return e.getClaims();
    }
  }

  public String extractUsername(String token) {

    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {

    return extractClaim(token, Claims::getExpiration);
  }

  public String extractIsRefreshClaim(String token) {
    final Claims claims = extractAllClaimsForException(token);
    try {
      return claims.get(TOKEN_TYPE, String.class);
    } catch (Exception e) {
      return null;
    }
  }

  public String generateAccessToken(UserDetails userDetails) {

    return generateAccessToken(new HashMap<>(), userDetails);
  }

  public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {

    return buildToken(extraClaims, userDetails, accessTokenExpiration);
  }

  public String generateRefreshToken(UserDetails userDetails) {
    Map<String, Object> extraClaims = new HashMap<>();
    extraClaims.put(TOKEN_TYPE, REFRESH_TOKEN);

    return generateRefreshToken(extraClaims, userDetails);
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

  private boolean isTokenValid(String token, UserDetails userDetails) {

    final String extractedUsername = extractUsername(token);

    return extractedUsername.equals(userDetails.getUsername());
  }

  private Key getSignInKey() {

    return Keys.hmacShaKeyFor(secretKey.getBytes());
  }

  public UserDetails loadUserDetails(String userEmail) {
    return customUserDetailsService.loadUserByUsername(userEmail);
  }

  public boolean isAccessTokenValid(String accessToken, UserDetails user) {

    return tokenRepository.findFirstByAccessToken(accessToken)
        .map(token -> !token.isAccessExpired() && !token.isAccessRevoked()).orElse(false);
  }

  public boolean isRefreshTokenValid(String refreshToken, UserDetails user) {

    return tokenRepository.findFirstByRefreshToken(refreshToken)
        .map(token -> !token.isRefreshExpired() && !token.isRefreshRevoked()).orElse(false);
  }
}
