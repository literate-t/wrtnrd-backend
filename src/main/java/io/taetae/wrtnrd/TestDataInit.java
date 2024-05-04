package io.taetae.wrtnrd;

import io.taetae.wrtnrd.domain.entity.Role;
import io.taetae.wrtnrd.domain.entity.User;
import io.taetae.wrtnrd.domain.entity.UserRole;
import io.taetae.wrtnrd.repository.RoleRepository;
import io.taetae.wrtnrd.repository.UserRepository;
import io.taetae.wrtnrd.repository.UserRoleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@RequiredArgsConstructor
public class TestDataInit {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  private final PasswordEncoder passwordEncoder;

  @EventListener(ApplicationReadyEvent.class)
  public void init() {

    log.info("Test data init");

    Role role = Role.create("ROLE_USER", "USER");
    roleRepository.save(Role.create("ROLE_ADMIN", "ADMIN"));
    roleRepository.save(Role.create("ROLE_MANAGER", "MANGER"));
    roleRepository.save(role);

    UserRole userRole = UserRole.create(role);

    User newUser = User.builder()
        .email("gaekoon@gmail.com")
        .password(passwordEncoder.encode("Rlaxogus@2022"))
        .userRoles(List.of(userRole))
        .build();

    userRole.setUser(newUser);

    userRepository.save(newUser);
    userRoleRepository.save(userRole);
  }
}
