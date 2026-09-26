package com.rasid.auth_service.dto;
import lombok.Builder;

import java.util.UUID;
@Builder
public record AuthResponseDto(
        UUID userId,
        String email,
        String role,
        String accessToken,
        String refreshToken
) {
}
