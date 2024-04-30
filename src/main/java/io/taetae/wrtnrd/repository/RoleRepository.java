package io.taetae.wrtnrd.repository;

import io.taetae.wrtnrd.domain.entity.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

  Optional<Role> findByRoleName(String roleName);
}
