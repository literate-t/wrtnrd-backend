package io.taetae.wrtnrd;

import io.taetae.wrtnrd.repository.RoleRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class WrtnrdBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(WrtnrdBackendApplication.class, args);
  }

  @Bean
  @Profile("dev")
  public TestDataInit testDataInit(RoleRepository repository) {
    return new TestDataInit(repository);
  }
}
