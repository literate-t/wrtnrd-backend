package io.taetae.wrtnrd.service;

import io.taetae.wrtnrd.domain.model.ProviderUser;
import io.taetae.wrtnrd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public void register(String clientRegistrationId, ProviderUser providerUser) {

  }
}
