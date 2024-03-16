package io.taetae.wrtnrd.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class Config {

//  @Order(0)
//  @Bean
//  SecurityFilterChain apiConfig(HttpSecurity http) throws Exception {
//
//    http.securityMatcher("/api/**")
//        .authorizeHttpRequests(registry -> {
//          registry.requestMatchers("/api/signup", "/api/signin").permitAll()
//              .anyRequest().authenticated();
//        });
//
//    return http.build();
//  }

//  @Order(1)
  @Bean
  SecurityFilterChain commonConfig(HttpSecurity http) throws Exception {

    http.authorizeHttpRequests(registry -> {
      registry.requestMatchers("/signup", "/signin").permitAll()
          .anyRequest().authenticated();
    });

    http.formLogin(Customizer.withDefaults());

    return http.build();
  }

}
