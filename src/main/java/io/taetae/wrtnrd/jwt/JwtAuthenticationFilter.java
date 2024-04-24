package io.taetae.wrtnrd.jwt;

import static io.taetae.wrtnrd.util.Constant.ACCESS_TOKEN;

import io.taetae.wrtnrd.repository.TokenRepository;
import io.taetae.wrtnrd.util.Util;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService customUserDetailsService;
  private final TokenRepository tokenRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String accessToken = Util.getCookieValue(request, ACCESS_TOKEN)
        .orElseThrow(() -> new BadRequestException("Bad request exception"));

    if (null == accessToken) {
      filterChain.doFilter(request, response);
      return;
    }

    String userEmail = jwtService.extractUsername(accessToken);
    // if an authentication already exists, this is unnecessary
    if (null != userEmail && null == SecurityContextHolder.getContext().getAuthentication()) {
      authenticate(request, userEmail, accessToken);
    }

    filterChain.doFilter(request, response);
  }

  private void authenticate(HttpServletRequest request, String userEmail, String accessToken) {

    UserDetails user = loadUserDetails(userEmail);

    if (isTokenAuthenticationValid(accessToken, user)) {
      setAuthenticationContext(request, user);
    }
  }

  private void setAuthenticationContext(HttpServletRequest request, UserDetails user) {

    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        user, null, user.getAuthorities()
    );

    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
  }

  private UserDetails loadUserDetails(String userEmail) {
    return customUserDetailsService.loadUserByUsername(userEmail);
  }

  private boolean isTokenAuthenticationValid(String accessToken, UserDetails user) {

    boolean isStoredAccessValid = tokenRepository.findByAccessToken(accessToken)
        .map(token -> !token.isAccessExpired() && !token.isAccessRevoked()).orElse(false);

    return isStoredAccessValid && jwtService.isTokenValid(accessToken, user);
  }
}
