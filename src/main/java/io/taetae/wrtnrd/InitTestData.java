package io.taetae.wrtnrd;

import io.taetae.wrtnrd.domain.entity.Role;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class InitTestData {

  private final EntityManager em;

  @EventListener(ApplicationReadyEvent.class)
  @Transactional
  public void init() {

    Role userRole = createRole("ROLE_USER", "user");
    Role managerRole = createRole("ROLE_MANAGER", "manager");
    Role adminRole = createRole("ROLE_ADMIN", "admin");

    em.persist(userRole);
    em.persist(managerRole);
    em.persist(adminRole);
  }

  private Role createRole(String name, String desc) {
    Role role = new Role();
    role.setName(name);
    role.setDescription(desc);

    return role;
  }
}
