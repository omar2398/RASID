package com.rasid.auth_service.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;
@Builder
public record UserResponseDto(
        UUID id,
        String username,
        String email,
        String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
