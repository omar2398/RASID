package com.rasid.auth_service.dto;

import com.rasid.auth_service.enumerate.Role;
import lombok.Builder;

@Builder
public record NewRoleDto(
        Role newRole
) {
}
