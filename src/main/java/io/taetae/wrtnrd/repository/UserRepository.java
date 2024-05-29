package io.taetae.wrtnrd.repository;

import io.taetae.wrtnrd.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);
  Optional<User> findByAuthor(String author);
}
