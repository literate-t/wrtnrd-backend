package io.taetae.wrtnrd.domain.dto;

public record UserResponseDto(Long id, String email, String author, String accessToken, String refreshToken) {}
