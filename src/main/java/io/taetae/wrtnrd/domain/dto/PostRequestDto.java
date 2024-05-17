package io.taetae.wrtnrd.domain.dto;

public record PostRequestDto(Long userId, String title, String body, String createdAt) {}
