package io.taetae.wrtnrd.repository;

import io.taetae.wrtnrd.domain.entity.Token;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {

  List<Token> findAllByUserId(Long userId);

  Optional<Token> findFirstByAccessToken(String accessToken);
  Optional<Token> findFirstByRefreshToken(String refreshToken);
}
