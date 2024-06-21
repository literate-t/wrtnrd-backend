package io.taetae.wrtnrd.service;

import static io.taetae.wrtnrd.enums.RoleEnum.ROLE_USER;
import static io.taetae.wrtnrd.util.Constant.REVOKE_ALL_PREVIOUS_TOKENS;

import io.taetae.wrtnrd.domain.dto.AuthenticationRequestDto;
import io.taetae.wrtnrd.domain.dto.AuthenticationResponseDto;
import io.taetae.wrtnrd.domain.dto.RegisterRequestDto;
import io.taetae.wrtnrd.domain.dto.RegisterResponseDto;
import io.taetae.wrtnrd.domain.entity.Role;
import io.taetae.wrtnrd.domain.entity.Token;
import io.taetae.wrtnrd.domain.entity.User;
import io.taetae.wrtnrd.domain.entity.UserRole;
import io.taetae.wrtnrd.filter.jwt.JwtService;
import io.taetae.wrtnrd.repository.RoleRepository;
import io.taetae.wrtnrd.repository.TokenRepository;
import io.taetae.wrtnrd.repository.UserRepository;
import io.taetae.wrtnrd.repository.UserRoleRepository;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
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
        .author(requestDto.author())
        .description(requestDto.description())
        .userRoles(List.of(userRole))
        .build();

    userRole.setUser(newUser);

    userRepository.save(newUser);
    userRoleRepository.save(userRole);

    return new RegisterResponseDto(requestDto.username());
  }

  @Transactional
  public AuthenticationResponseDto authenticate(AuthenticationRequestDto requestDto) {

    String username = requestDto.username();
    String password = requestDto.password();

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password));

    User findUser = userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    String accessToken = jwtService.generateAccessToken(findUser);
    String refreshToken = jwtService.generateRefreshToken(findUser);

    boolean result = revokeAllPreviousUserToken(findUser.getId());

    if (result) {
      saveUserToken(findUser, accessToken, refreshToken);
      return new AuthenticationResponseDto(findUser, accessToken, refreshToken);
    }
    else {
      return null;
    }
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

  @Transactional
  public boolean revokeAllPreviousUserToken(Long userId) {

    try {
      List<Token> allTokens = tokenRepository.findAllByUserId(userId);

      if (allTokens.isEmpty()) {
        return true;
      }

      allTokens.forEach(Token::invalidate);
      log.info(REVOKE_ALL_PREVIOUS_TOKENS);
      return true;
    } catch(Exception e) {
      log.error(e.getMessage());
      return false;
    }
  }

  @Transactional
  public AuthenticationResponseDto createNewTokens(String jwt)
      throws IOException {

    try {
      String username = jwtService.extractUsername(jwt);
      AuthenticationResponseDto dto = refreshAllToken(username);
      log.info("Token refresh successful: {}, {}, {}", username, dto.accessToken(), dto.refreshToken());

      return dto;
    } catch (AuthenticationException e) {
      return null;
    }
  }

  @Transactional
  public AuthenticationResponseDto refreshAllToken(String username)
      throws IOException {

    User user = findUser(username);

      boolean result = revokeAllPreviousUserToken(user.getId());

      if (result) {
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(user, newAccessToken, newRefreshToken);

        return new AuthenticationResponseDto(newAccessToken, newRefreshToken);
      }
      else {
        return null;
      }
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
