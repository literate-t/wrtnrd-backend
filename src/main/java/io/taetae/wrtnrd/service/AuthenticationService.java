package io.taetae.wrtnrd.service;

import static io.taetae.wrtnrd.enums.RoleEnum.ROLE_USER;
import static io.taetae.wrtnrd.util.Constant.REFRESH_TOKEN;

import io.taetae.wrtnrd.domain.dto.AuthenticationRequestDto;
import io.taetae.wrtnrd.domain.dto.AuthenticationResponseDto;
import io.taetae.wrtnrd.domain.dto.RegisterRequestDto;
import io.taetae.wrtnrd.domain.dto.RegisterResponseDto;
import io.taetae.wrtnrd.domain.entity.Role;
import io.taetae.wrtnrd.domain.entity.Token;
import io.taetae.wrtnrd.domain.entity.User;
import io.taetae.wrtnrd.domain.entity.UserRole;
import io.taetae.wrtnrd.jwt.JwtService;
import io.taetae.wrtnrd.repository.RoleRepository;
import io.taetae.wrtnrd.repository.TokenRepository;
import io.taetae.wrtnrd.repository.UserRepository;
import io.taetae.wrtnrd.repository.UserRoleRepository;
import io.taetae.wrtnrd.util.Util;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationService {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public RegisterResponseDto register(RegisterRequestDto requestDto) {

   Role role;

   try {
     role = roleRepository.findByRoleName(ROLE_USER.name())
         .orElseThrow(() -> new BadRequestException("Bad request exception"));
   } catch (BadRequestException e) {
     log.error("Bad user role exception");
     throw new RuntimeException(e);
   }

    UserRole userRole = UserRole.create(role);

    User newUser = User.builder()
        .email(requestDto.username())
        .password(passwordEncoder.encode(requestDto.password()))
        .userRoles(List.of(userRole))
        .build();

    userRole.setUser(newUser);

    userRepository.save(newUser);
    userRoleRepository.save(userRole);

    return new RegisterResponseDto(requestDto.username());
  }

  public AuthenticationResponseDto authenticate(AuthenticationRequestDto requestDto) {

    String username = requestDto.username();
    String password = requestDto.password();

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password));

    User findUser = userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    String accessToken = jwtService.generateAccessToken(findUser);
    String refreshToken = jwtService.generateRefreshToken(findUser);

    revokeAllPreviousUserToken(findUser);

    saveUserToken(findUser, accessToken, refreshToken);

    return new AuthenticationResponseDto(accessToken, refreshToken);
  }

  private void saveUserToken(User user, String accessToken, String refreshToken) {

    Token token = Token.builder()
        .user(user)
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .accessExpired(false)
        .accessRevoked(false)
        .refreshExpired(false)
        .refreshRevoked(false)
        .build();

    tokenRepository.save(token);
  }

  private void revokeAllPreviousUserToken(User user) {

    List<Token> allTokens = tokenRepository.findAllByUserId(user.getId());

    if (allTokens.isEmpty()) {
      return;
    }

    allTokens.forEach(Token::invalidate);

    tokenRepository.saveAll(allTokens);
  }

  public void refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    String refreshToken = null;

    try {
      refreshToken = Util.checkCookieAndGetValue(request, REFRESH_TOKEN);

      // if token is expired, is throws an exception
      String username = jwtService.extractUsername(refreshToken);

      if (null != username) {
        refreshAllToken(username, refreshToken, request, response);
      }
    } catch (BadRequestException e) {
      Util.sendResponse(response, ResponseEntity.badRequest());
    }
  }

  private void refreshAllToken(String username, String refreshToken, HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    User user = findUser(username);

    if (isTokenAuthenticationValid(refreshToken, user)) {

      String newAccessToken = jwtService.generateAccessToken(user);
      String newRefreshToken = jwtService.generateRefreshToken(user);

      revokeAllPreviousUserToken(user);
      saveUserToken(user, newAccessToken, newRefreshToken);

      AuthenticationResponseDto responseDto = new AuthenticationResponseDto(
          newAccessToken, newRefreshToken);

      Util.sendResponse(response, responseDto);
    }
  }

  private User findUser(String username) {
    return userRepository.findByEmail(username).orElseThrow();
  }

  private boolean isTokenAuthenticationValid(String refreshToken, UserDetails user) {

    boolean isStoredTokenValid = tokenRepository.findByRefreshToken(refreshToken)
        .map(token -> !token.isRefreshExpired() && !token.isRefreshRevoked()).orElse(false);

    return isStoredTokenValid && jwtService.isTokenValid(refreshToken, user);
  }

  public boolean isDuplicateEmail(String email) {

    User user = userRepository.findByEmail(email).orElse(null);

    return null != user;
  }
}
