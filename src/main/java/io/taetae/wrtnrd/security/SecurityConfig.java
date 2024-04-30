package io.taetae.wrtnrd.security;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import io.taetae.wrtnrd.jwt.JwtAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

  private final AuthenticationProvider authenticationProvider;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  SecurityFilterChain apiConfig(HttpSecurity http) throws Exception {

    http.securityMatcher("/api/auth/**")
        .authorizeHttpRequests(registry -> {
          registry.requestMatchers("/api/auth/register", "/api/auth/authenticate", "/api/auth/refresh-token").permitAll()
              .anyRequest().authenticated();
        });

    http.sessionManagement(config -> config.sessionCreationPolicy(STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .cors((config -> config.configurationSource(corsConfigurationSource())))
// TODO
//        .logout(config -> config
//            .addLogoutHandler(null)
//            .logoutSuccessHandler(
//                ((request, response, authentication) -> SecurityContextHolder.clearContext()))
//            .logoutUrl("/api/auth/logout")
//        )
        ;

    http.csrf(AbstractHttpConfigurer::disable);

    return http.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:3000"));
    config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
    config.setAllowedHeaders(List.of("Content-Type"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return source;
  }
}
