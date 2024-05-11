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

// TODO token 필드를 하나만 두는 대신에 어떤 토큰인지 타입을 넣을 것
// ac, rf 검증 로직이 거의 동일한데 지금 구조에선 검증 로직을 각각 둬야 하는 불필요함이 존재
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
