package io.taetae.wrtnrd.domain.dto;

import java.util.List;

public record UserDto(String username, String password, String clientRegistrationId, String provider, List<String> roles) {
}
