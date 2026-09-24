package com.rasid.auth_service.dto;

import jakarta.validation.constraints.Email;

public record UserRequestDto(
        String username,
        @Email
        String email,
        String password
) {
}
