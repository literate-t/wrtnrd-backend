package io.taetae.wrtnrd.repository;

import io.taetae.wrtnrd.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
