package io.taetae.wrtnrd.service;

import io.taetae.wrtnrd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public boolean checkPassword(String username, String password) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(username, password));
      return true;
    } catch (AuthenticationException e) {
      return false;
    }
  }

  @Transactional
  public boolean changeUserPassword(String userEmail, String newPassword) {

    return userRepository.findByEmail(userEmail).map(user -> {
      user.setPassword(passwordEncoder.encode(newPassword));
      log.info("Password changed for user: {}", userEmail);
      return true;
    }).orElseGet(() -> {
      log.warn("No user found for email: {}", userEmail);
      return false;
    });
  }

  public boolean isAuthorAvailable(String author) {

    return userRepository.findByAuthor(author).map((user) -> {
      log.warn("author exists: {}", user.getAuthor());
      return false;
    }).orElseGet(() -> {
      log.warn("author doesn't exist: {}", author);
      return true;
    });
  }

  @Transactional
  public boolean changeAuthor(Long id, String author) {

    return userRepository.findById(id).map(user -> {
      user.setAuthor(author);
      log.info("Author changed for user: {}", author);
      return true;
    }).orElseGet(()-> {
      log.warn("Author update failure: {}", author);
      return false;
    });
  }
}
