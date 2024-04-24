package io.taetae.wrtnrd;

import io.taetae.wrtnrd.domain.entity.Role;
import io.taetae.wrtnrd.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@RequiredArgsConstructor
public class TestDataInit {

  private final RoleRepository roleRepository;

  @EventListener(ApplicationReadyEvent.class)
  public void init() {

    log.info("Test data init");

    roleRepository.save(Role.create("ROLE_ADMIN", "ADMIN"));
    roleRepository.save(Role.create("ROLE_MANAGER", "MANGER"));
    roleRepository.save(Role.create("ROLE_USER", "USER"));
  }
}
