package io.taetae.wrtnrd.domain.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.ToString;

@Entity
@Getter
@ToString
public class User {

  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "user_id")
  private Long id;
  private String username;
  private String password;
  private String email;
  @Nullable
  private String clientRegistrationId;
  @Nullable
  private String provider;
  @ToString.Exclude
  @OneToMany(mappedBy = "user")
  List<UserRole> userRoles = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(getId(), user.getId()) && Objects.equals(getUsername(),
        user.getUsername()) && Objects.equals(getPassword(), user.getPassword())
        && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(
        getClientRegistrationId(), user.getClientRegistrationId()) && Objects.equals(
        getProvider(), user.getProvider()) && Objects.equals(getUserRoles(),
        user.getUserRoles());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getUsername(), getPassword(), getEmail(),
        getClientRegistrationId(),
        getProvider(), getUserRoles());
  }
}
