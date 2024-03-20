package io.taetae.wrtnrd.security;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class Config {

  private final UserDetailsService customUserDetailsService;

  @Order(0)
  @Bean
  SecurityFilterChain apiConfig(HttpSecurity http) throws Exception {

    http.securityMatcher("/api/**")
        .authorizeHttpRequests(registry -> {
          registry.requestMatchers("/api/csrfToken").permitAll()
              .anyRequest().authenticated();
        });

    return http.build();
  }

  @Order(1)
  @Bean
  SecurityFilterChain commonConfig(HttpSecurity http) throws Exception {

    http.authorizeHttpRequests(registry -> {
      registry.requestMatchers("/signup", "/signin").permitAll()
          .anyRequest().authenticated();
    });

    http.formLogin(Customizer.withDefaults());

    http.userDetailsService(customUserDetailsService);

    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
    corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    corsConfiguration.setAllowedHeaders(List.of("*"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);

    return source;
  }
}
