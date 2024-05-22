package io.taetae.wrtnrd.domain.dto;

public record PostResponseDto(Long id, String title, String author, String description, String body, String createdAt) {

}
