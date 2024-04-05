package io.taetae.wrtnrd.service;

import io.taetae.wrtnrd.domain.entity.User;
import io.taetae.wrtnrd.domain.entity.UserRole;
import io.taetae.wrtnrd.domain.model.PrincipalUser;
import io.taetae.wrtnrd.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService extends AbstractUserService implements UserDetailsService {

//  private final UserService userService;
  private final UserRepository userRepository;


  @Transactional
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    if (null == user.getId()) {
      throw new UsernameNotFoundException("User not found exception");
    }

    // TODO 비밀번호 확인

    List<GrantedAuthority> roles = new ArrayList<>();
    for (UserRole userRole : user.getUserRoles()) {
      roles.add(new SimpleGrantedAuthority(userRole.getRole().getName()));
    }

    return new PrincipalUser(user, roles);
  }
}
