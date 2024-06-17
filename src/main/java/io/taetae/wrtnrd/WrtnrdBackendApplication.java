package io.taetae.wrtnrd;

import io.taetae.wrtnrd.repository.PostRepository;
import io.taetae.wrtnrd.repository.RoleRepository;
import io.taetae.wrtnrd.repository.UserRepository;
import io.taetae.wrtnrd.repository.UserRoleRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class WrtnrdBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(WrtnrdBackendApplication.class, args);
  }

  @Bean
  @Profile("prod")
  public DataInit dataInit(RoleRepository roleRepository) {
    return new DataInit(roleRepository);
  }

  @Bean
  @Profile("dev")
  public TestDataInit testDataInit(RoleRepository repository, UserRepository userRepository, UserRoleRepository userRoleRepository, PostRepository postRepository, PasswordEncoder passwordEncoder) {
    return new TestDataInit(repository, userRepository, userRoleRepository, postRepository, passwordEncoder);
  }
}
