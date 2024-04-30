package io.taetae.wrtnrd.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {

  @Id @GeneratedValue
  private Long id;

  private String accessToken;

  private String refreshToken;

  private boolean accessExpired;

  private boolean accessRevoked;

  private boolean refreshExpired;

  private boolean refreshRevoked;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  public void invalidate() {
    accessExpired = true;
    accessRevoked = true;
    refreshExpired = true;
    refreshRevoked = true;
  }
}
