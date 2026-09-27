package com.rasid.auth_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ChangePasswordRequestDto(
        @NotNull
        String oldPassword,
        @NotNull
        @Min(value = 8)
        String newPassword
) {
}
