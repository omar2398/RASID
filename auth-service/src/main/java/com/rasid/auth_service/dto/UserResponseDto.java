package com.rasid.auth_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String username,
        String email,
        String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
