package io.taetae.wrtnrd.domain.dto;

public record AuthenticationRequestDto(String username, String password, String author, String description) {}
