package io.taetae.wrtnrd.jwt;

import static io.taetae.wrtnrd.util.Constant.ACCESS_TOKEN;
import static io.taetae.wrtnrd.util.Url.REGISTER_URL;
import static io.taetae.wrtnrd.util.Util.checkSameString;

import io.taetae.wrtnrd.repository.TokenRepository;
import io.taetae.wrtnrd.util.Util;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    String accessToken = Util.checkCookieAndGetValue(request, ACCESS_TOKEN);
    String targetUrl = getRequestURI(request);

    boolean isRegisterUrl = checkSameString(targetUrl, REGISTER_URL);

    // if user has an old access token, treat as new user
    if (null == accessToken || isRegisterUrl) {
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

  private String getRequestURI(HttpServletRequest request) {
    return request.getRequestURI();
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
