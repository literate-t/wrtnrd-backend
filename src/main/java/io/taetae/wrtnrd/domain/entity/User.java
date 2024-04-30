package io.taetae.wrtnrd.domain.entity;

import static jakarta.persistence.FetchType.EAGER;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
@Entity(name = "USERS")
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
  @OneToMany(mappedBy = "user", fetch = EAGER)
  List<UserRole> userRoles = new ArrayList<>();

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return getUserRoles().stream()
        .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getRoleName()))
        .collect(Collectors.toList());
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
