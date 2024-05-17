package io.taetae.wrtnrd.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.taetae.wrtnrd.domain.entity.User;

public record AuthenticationResponseDto(User user, @JsonProperty("ac")String accessToken, @JsonProperty("rf")String refreshToken) {

  public AuthenticationResponseDto(String accessToken, String refreshToken) {
    this(null, accessToken, refreshToken);
  }
}
