package com.rasid.auth_service.dto;

public record ChangePasswordRequestDto(
        String oldPassword,
        String newPassword
) {
}
