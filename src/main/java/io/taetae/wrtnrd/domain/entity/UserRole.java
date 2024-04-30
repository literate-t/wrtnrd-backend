package io.taetae.wrtnrd.domain.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserRole {

  @Id
  @GeneratedValue(strategy = SEQUENCE)
  @Column(name = "user_role_id")
  private Long id;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ToString.Exclude
  @ManyToOne
  @JoinColumn(name = "role_id")
  private Role role;

  private LocalDateTime createdAt;

  public static UserRole create(Role role) {
    return new UserRole(null, null, role, LocalDateTime.now());
  }
}
