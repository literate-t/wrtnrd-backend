package io.taetae.wrtnrd.common;

import io.taetae.wrtnrd.domain.model.ProviderUser;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DelegatingProviderUserConverter {

  List<ProviderUserConverter<ProviderUserRequestDto, ProviderUser>> converters;

  public DelegatingProviderUserConverter() {

  }
}
