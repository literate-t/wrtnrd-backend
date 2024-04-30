package io.taetae.wrtnrd.repository;

import io.taetae.wrtnrd.domain.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

}
