package io.taetae.wrtnrd.service;

import io.taetae.wrtnrd.domain.dto.UserDto;
import io.taetae.wrtnrd.domain.entity.Role;
import io.taetae.wrtnrd.domain.entity.User;
import io.taetae.wrtnrd.domain.entity.UserRole;
import io.taetae.wrtnrd.domain.model.ProviderUser;
import io.taetae.wrtnrd.repository.RoleRepository;
import io.taetae.wrtnrd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final RoleRepository repository;
  private final PasswordEncoder passwordEncoder;

  public void register(String clientRegistrationId, ProviderUser providerUser) {

  }

  @Transactional
  public void register(UserDto userDto) {

    Role roleUser = repository.findByName("ROLE_USER");
    UserRole userRole = UserRole.create(roleUser);
    User user = User.create(userDto, userRole);

    changeToEncodedPassword(userDto, user);

    userRepository.save(user);
  }

  private void changeToEncodedPassword(UserDto userDto, User user) {
    String encoded = passwordEncoder.encode(userDto.password());
    user.changePassword(encoded);
  }
}

