package io.taetae.wrtnrd.filter.jwt;

import static io.taetae.wrtnrd.util.Constant.REFRESH_TOKEN;
import static io.taetae.wrtnrd.util.Url.LOGOUT_URL;
import static io.taetae.wrtnrd.util.Url.REGISTER_URL;
import static io.taetae.wrtnrd.util.Util.checkSameString;
import static io.taetae.wrtnrd.util.Util.getBearerToken;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// TODO Token에 ac, rf 등으로 타입을 두고 필드를 token 하나만 두면 중복 로직을 통합할 것
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    String jwt = getBearerToken(request.getHeader(AUTHORIZATION));
    String targetUrl = request.getRequestURI();

    boolean isRegisterUrl = checkSameString(targetUrl, REGISTER_URL);

    // if user has an old access token, treat as new user
    if (jwt.isEmpty() || isRegisterUrl || targetUrl.equals(LOGOUT_URL)) {
      filterChain.doFilter(request, response);
      return;
    }

    String userEmail;
    try {
      userEmail = jwtService.extractUsername(jwt);
    } catch (ExpiredJwtException e1) {

      try {
        String tokenType = jwtService.extractIsRefreshClaim(jwt);
        if (checkSameString(tokenType, REFRESH_TOKEN)) {
          response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } else {
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
      } catch(Exception e) {
        log.error(e.getMessage());
      }

      return;
    }

    if (null != userEmail && null == SecurityContextHolder.getContext().getAuthentication()) {
      authenticate(request, userEmail, jwt);
    }

    filterChain.doFilter(request, response);
  }

  private void authenticate(HttpServletRequest request, String userEmail, String token) {

    UserDetails user = jwtService.loadUserDetails(userEmail);

    if (jwtService.isAccessTokenValid(token, user) || jwtService.isRefreshTokenValid(token, user)) {
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
}
