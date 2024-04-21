package io.taetae.wrtnrd.domain.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@ToString
public class User implements UserDetails {

  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;
  private String email;
  private String password;
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
    return Objects.equals(getId(), user.getId()) && Objects.equals(getPassword(), user.getPassword())
        && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(
        getClientRegistrationId(), user.getClientRegistrationId()) && Objects.equals(
        getProvider(), user.getProvider()) && Objects.equals(getUserRoles(),
        user.getUserRoles());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getPassword(), getEmail(),
        getClientRegistrationId(),
        getProvider(), getUserRoles());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return getUserRoles().stream()
        .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getName()))
        .collect(Collectors.toList());
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }
}
