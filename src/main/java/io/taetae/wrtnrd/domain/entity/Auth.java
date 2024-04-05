package io.taetae.wrtnrd.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Auth {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(nullable = false)
  private String tokenType;

  @Column(nullable = false)
  private String refreshToken;

  @Builder
  public Auth(String tokenType, String refreshToken) {

    this.tokenType = tokenType;
    this.refreshToken = refreshToken;
  }
}
