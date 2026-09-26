package com.rasid.auth_service.dto;

import lombok.Builder;

@Builder
public record UserLoginRequestDto(
        String email,
        String password
) {
}
