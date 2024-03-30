package io.taetae.wrtnrd.security;

import io.taetae.wrtnrd.handler.CustomAuthenticationSuccessHandler;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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
          registry
              .anyRequest().authenticated();
        });

    http.cors(config -> config.configurationSource(corsConfigurationSource()));

    http.csrf(AbstractHttpConfigurer::disable);

    return http.build();
  }

  @Order(1)
  @Bean
  SecurityFilterChain commonConfig(HttpSecurity http) throws Exception {

    http.authorizeHttpRequests(registry -> {
      registry.requestMatchers("/signup", "/login").permitAll()
          .anyRequest().authenticated();
    });

    http.formLogin(config ->
        config.successHandler(authenticationSuccessHandler())
    );

    http.cors(config -> config.configurationSource(corsConfigurationSource()));

    http.csrf(AbstractHttpConfigurer::disable);

    http.sessionManagement(config ->
        config.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    );

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
    corsConfiguration.setAllowCredentials(true);
    corsConfiguration.setAllowedHeaders(List.of("*"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);

    return source;
  }

  @Bean
  public AuthenticationSuccessHandler authenticationSuccessHandler() {
    return new CustomAuthenticationSuccessHandler();
  }
}
