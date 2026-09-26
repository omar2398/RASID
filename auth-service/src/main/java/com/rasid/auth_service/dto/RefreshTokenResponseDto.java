package com.rasid.auth_service.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RefreshTokenResponseDto(
        String refreshToken,
        String newAccessToken,
        UUID userId,
        String role
) {
}
