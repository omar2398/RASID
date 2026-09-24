package com.rasid.auth_service.dto;
import java.util.UUID;

public record AuthResponseDto(
        UUID userId,
        String email,
        String role,
        String accessToken,
        String refreshToken
) {
}
