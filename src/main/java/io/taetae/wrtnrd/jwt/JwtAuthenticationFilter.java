package io.taetae.wrtnrd.jwt;

import static io.taetae.wrtnrd.util.Constant.ACCESS_TOKEN;
import static io.taetae.wrtnrd.util.Constant.REFRESH_TOKEN;
import static io.taetae.wrtnrd.util.Url.NEW_TOKENS_URL;
import static io.taetae.wrtnrd.util.Url.REGISTER_URL;
import static io.taetae.wrtnrd.util.Util.checkSameString;

import io.taetae.wrtnrd.util.Util;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// TODO Token에 ac, rf 등으로 타입을 두고 필드를 token 하나만 두면 중복 로직을 통합할 것

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    String accessToken = Util.checkCookieAndGetValue(request, ACCESS_TOKEN);
    String refreshToken = Util.checkCookieAndGetValue(request, REFRESH_TOKEN);
    String targetUrl = getRequestURI(request);

    boolean isRegisterUrl = checkSameString(targetUrl, REGISTER_URL);

    // if user has an old access token, treat as new user
    if (null == accessToken || isRegisterUrl) {
      filterChain.doFilter(request, response);
      return;
    }

    String userEmail = null;
    try {
      userEmail = jwtService.extractUsername(accessToken);
    } catch (RuntimeException e) {

      if (checkSameString(targetUrl, NEW_TOKENS_URL)) {
        try {
          userEmail = jwtService.extractUsername(refreshToken);

          if (null != userEmail && null == SecurityContextHolder.getContext().getAuthentication()) {
            authenticateWithRefreshToken(request, userEmail, refreshToken);
          }

          filterChain.doFilter(request, response);

          return;
        } catch(RuntimeException nestedException) {
          deleteTokenCookies(request, response);
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

          return;
        }
      }

      response.sendError(HttpServletResponse.SC_FORBIDDEN);

      return;
    }

    if (null != userEmail && null == SecurityContextHolder.getContext().getAuthentication()) {
      authenticateWithAccessToken(request, userEmail, accessToken);
    }

    filterChain.doFilter(request, response);
  }

  private void deleteTokenCookies(HttpServletRequest request, HttpServletResponse response) {

    Cookie accessTokenCookie = Util.getCookie(request, ACCESS_TOKEN).orElse(null);
    Cookie refreshTokenCookie = Util.getCookie(request, REFRESH_TOKEN).orElse(null);

    if (null != accessTokenCookie) {
      accessTokenCookie.setMaxAge(0);
      accessTokenCookie.setPath("/");
      response.addCookie(accessTokenCookie);
    }
    if (null != refreshTokenCookie) {
      refreshTokenCookie.setMaxAge(0);
      refreshTokenCookie.setPath("/");
      response.addCookie(refreshTokenCookie);
    }
  }

  private String getRequestURI(HttpServletRequest request) {
    return request.getRequestURI();
  }

  private void authenticateWithAccessToken(HttpServletRequest request, String userEmail, String accessToken) {

    UserDetails user = jwtService.loadUserDetails(userEmail);

    if (jwtService.isAccessTokenValid(accessToken, user)) {
      setAuthenticationContext(request, user);
    }
  }

  private void authenticateWithRefreshToken(HttpServletRequest request, String userEmail, String refreshToken) {

    UserDetails user = jwtService.loadUserDetails(userEmail);

    if (jwtService.isRefreshTokenValid(refreshToken, user)) {
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
