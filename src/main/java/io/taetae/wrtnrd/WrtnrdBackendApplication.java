package io.taetae.wrtnrd;

import jakarta.persistence.EntityManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WrtnrdBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(WrtnrdBackendApplication.class, args);
  }

  @Bean
  public InitTestData initTestData(EntityManager entityManager) {
    return new InitTestData(entityManager);
  }
}
