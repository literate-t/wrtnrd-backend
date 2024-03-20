package io.taetae.wrtnrd.domain.model.user;

import io.taetae.wrtnrd.domain.model.ProviderUser;
import java.util.List;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;

@Builder
public class FormUser implements ProviderUser {

  private Long id;
  private String username;
  private String password;
  private String provider;
  private List<? extends GrantedAuthority> authorities;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getProvider() {
    return provider;
  }

  @Override
  public List<? extends GrantedAuthority> getAuthorities() {
    return null;
  }
}
