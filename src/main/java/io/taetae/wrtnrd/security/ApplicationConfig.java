package io.taetae.wrtnrd.security;

import io.taetae.wrtnrd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

  private final UserRepository userRepository;

//  @Bean
//  public AuthenticationProvider authenticationProvider() {
//    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//  }

  @Bean
  public UserDetailsService userDetailsService() {

    return username -> userRepository.findByEmail(username).orElse(null);
  }

}
