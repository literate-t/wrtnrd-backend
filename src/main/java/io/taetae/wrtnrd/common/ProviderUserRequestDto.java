package io.taetae.wrtnrd.common;

import io.taetae.wrtnrd.domain.entity.User;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public record ProviderUserRequestDto(ClientRegistration clientRegistration, OAuth2User oAuth2User, User user) {

  public ProviderUserRequestDto(ClientRegistration clientRegistration, OAuth2User oAuth2User) {
    this(clientRegistration, oAuth2User, null);
  }

  public ProviderUserRequestDto(User user) {
    this(null, null, user);
  }
}
