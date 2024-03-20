package io.taetae.wrtnrd.domain.model;

import java.util.List;
import org.springframework.security.core.GrantedAuthority;

public interface ProviderUser {

  Long getId();
  String getUsername();
  String getPassword();
  String getProvider();
  List<? extends GrantedAuthority> getAuthorities();
}
