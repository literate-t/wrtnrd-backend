package io.taetae.wrtnrd.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthenticationResponseDto(@JsonProperty("ac")String accessToken, @JsonProperty("rf")String refreshToken) {}
