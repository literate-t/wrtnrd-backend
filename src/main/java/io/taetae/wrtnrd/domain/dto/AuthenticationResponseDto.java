package io.taetae.wrtnrd.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthenticationResponseDto(@JsonProperty("access-token")String accessToken, @JsonProperty("refresh-token")String refreshToken) {}
