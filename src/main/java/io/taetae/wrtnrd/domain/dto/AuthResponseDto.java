package io.taetae.wrtnrd.domain.dto;

public record AuthResponseDto(String accessToken, String refreshToken, String tokenType) {}