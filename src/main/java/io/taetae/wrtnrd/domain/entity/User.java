package io.taetae.wrtnrd.domain.entity;

import static jakarta.persistence.CascadeType.ALL;

import io.taetae.wrtnrd.domain.dto.UserDto;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;
  private String username;
  private String password;
  @Nullable
  private String clientRegistrationId;
  @Nullable
  private String provider;
  @ToString.Exclude
  @OneToMany(mappedBy = "user", cascade = ALL)
  List<UserRole> userRoles = new ArrayList<>();

  public User(UserDto userDto, UserRole... userRoles) {

    this.username = userDto.username();
    this.password = userDto.password();
    this.clientRegistrationId = userDto.clientRegistrationId();
    this.provider = userDto.provider();

    for (UserRole userRole : userRoles) {
      addUserRole(userRole);
    }
  }

  public static User create(UserDto userDto, UserRole ...userRole) {

    return new User(userDto, userRole);
  }

  private void addUserRole(UserRole userRole) {
    userRole.setUser(this);
    userRoles.add(userRole);
  }

  public void changePassword(String encoded) {
    password = encoded;
  }

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
        && Objects.equals(getClientRegistrationId(), user.getClientRegistrationId()) && Objects.equals(
        getProvider(), user.getProvider()) && Objects.equals(getUserRoles(),
        user.getUserRoles());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getUsername(), getPassword(), getClientRegistrationId(),
        getProvider(), getUserRoles());
  }
}
