package io.taetae.wrtnrd.security;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import io.taetae.wrtnrd.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${app.frontend-url}")
  private String frontendUrl;

  @Bean
  SecurityFilterChain apiConfig(HttpSecurity http) throws Exception {

    http.securityMatcher("/api/**")
        .authorizeHttpRequests(registry -> {
          registry.requestMatchers("/api/auth/register", "/api/auth/authenticate",
                  "/api/post/list").permitAll() // "/api/auth/new-token"
              .anyRequest().authenticated();
        });

    http.sessionManagement(config -> config.sessionCreationPolicy(STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .cors((config -> config.configurationSource(corsConfigurationSource())))
        .exceptionHandling(config -> config.authenticationEntryPoint(((request, response, authException) -> {
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Denied: Authentication is required to access this resource.");
        })))
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
    config.setAllowedOrigins(List.of(frontendUrl));
    config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
    config.setAllowedHeaders(List.of("Content-Type"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return source;
  }
}
