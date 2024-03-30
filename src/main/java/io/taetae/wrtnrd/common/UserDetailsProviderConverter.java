package io.taetae.wrtnrd.common;

import io.taetae.wrtnrd.domain.entity.User;
import io.taetae.wrtnrd.domain.model.ProviderUser;
import io.taetae.wrtnrd.domain.model.user.FormUser;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserDetailsProviderConverter implements ProviderUserConverter<ProviderUserRequestDto, ProviderUser> {

  @Override
  public ProviderUser convert(ProviderUserRequestDto providerUserRequestDto) {

    if (null == providerUserRequestDto.user()) {
      return null;
    }

    User user = providerUserRequestDto.user();

    return FormUser.builder()
        .id(user.getId())
        .username(user.getUsername())
        .password(user.getPassword())
        .authorities(user.getUserRoles().stream()
            .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getName())).collect(
                Collectors.toList()))
        .provider(null)
        .build();
  }
}
