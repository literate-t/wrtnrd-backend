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
import org.springframework.transaction.annotation.Transactional;

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

    return new AuthenticationResponseDto(findUser, accessToken, refreshToken);
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

  @Transactional
  public AuthenticationResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    AuthenticationResponseDto responseDto = null;

    try {
      String refreshToken = Util.checkCookieAndGetValue(request, REFRESH_TOKEN);

      // if token is expired, is throws an exception
      String username = jwtService.extractUsername(refreshToken);

      if (null != username) {
        responseDto = refreshAllToken(username);
      }
    } catch (BadRequestException e) {
      Util.sendResponse(response, ResponseEntity.badRequest());
    }

    return responseDto;
  }

  private AuthenticationResponseDto refreshAllToken(String username)
      throws IOException {

    User user = findUser(username);

      revokeAllPreviousUserToken(user);

      String newAccessToken = jwtService.generateAccessToken(user);
      String newRefreshToken = jwtService.generateRefreshToken(user);

      saveUserToken(user, newAccessToken, newRefreshToken);

      return new AuthenticationResponseDto(newAccessToken, newRefreshToken);
  }

  private User findUser(String username) {
    return userRepository.findByEmail(username).orElseThrow();
  }

  private boolean isRefreshTokenValid(String refreshToken, UserDetails user) {

    return jwtService.isRefreshTokenValid(refreshToken, user);
  }

  public boolean isDuplicateEmail(String email) {

    User user = userRepository.findByEmail(email).orElse(null);

    return null != user;
  }
}
