package io.taetae.wrtnrd.domain.model;


import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class PrincipalUser extends User {

  io.taetae.wrtnrd.domain.entity.User user;

  public PrincipalUser(io.taetae.wrtnrd.domain.entity.User user,
      Collection<? extends GrantedAuthority> authorities) {
    super(user.getUsername(), user.getPassword(), authorities);
    this.user = user;
  }

  public PrincipalUser(io.taetae.wrtnrd.domain.entity.User user,boolean enabled, boolean accountNonExpired,
      boolean credentialsNonExpired, boolean accountNonLocked,
      Collection<? extends GrantedAuthority> authorities) {
    super(user.getUsername(), user.getPassword(), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
        authorities);
    this.user = user;
  }

  public Long getId() {
    return user.getId();
  }
}
