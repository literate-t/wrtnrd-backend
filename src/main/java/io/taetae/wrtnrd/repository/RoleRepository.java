package io.taetae.wrtnrd.repository;

import io.taetae.wrtnrd.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
