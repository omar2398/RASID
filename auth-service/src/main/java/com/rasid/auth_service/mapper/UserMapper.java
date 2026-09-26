package com.rasid.auth_service.mapper;

import com.rasid.auth_service.dto.AuthResponseDto;
import com.rasid.auth_service.dto.RefreshTokenResponseDto;
import com.rasid.auth_service.dto.UserRequestDto;
import com.rasid.auth_service.dto.UserResponseDto;
import com.rasid.auth_service.entity.RefreshToken;
import com.rasid.auth_service.entity.User;
import com.rasid.auth_service.enumerate.Role;
import com.rasid.auth_service.util.JWTUtils;
import com.rasid.auth_service.util.TokenHasher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class UserMapper {
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final TokenHasher tokenHasher;

    public UserMapper(PasswordEncoder passwordEncoder, JWTUtils utils, TokenHasher tokenHasher) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = utils;
        this.tokenHasher = tokenHasher;
    }

    public User toEntity(UserRequestDto dto) {
        return User.builder()
                .role(Role.VIEWER)
                .email(dto.email())
                .isActive(true)
                .password(passwordEncoder.encode(dto.password()))
                .username(dto.username())
                .build();
    }

    public UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .username(user.getUsername())
                .build();
    }

    public AuthResponseDto toAuthResponse(User user) {
        return AuthResponseDto
                .builder()
                .email(user.getEmail())
                .role(user.getRole().toString())
                .userId(user.getId())
                .accessToken(jwtUtils.generateAccessToken(user))
                .refreshToken(jwtUtils.generateRefreshToken(user))
                .build();
    }

    public RefreshToken toRefreshTokenEntity(AuthResponseDto response, User user) {
        return RefreshToken.builder()
                .tokenHash(tokenHasher.hash(response.refreshToken()))
                .expiresAt(jwtUtils.getRefreshTokenExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .user(user)
                .id(user.getId())
                .build();
    }

    public RefreshTokenResponseDto toRefreshTokenResponse(User user, String refreshToken) {
        return RefreshTokenResponseDto
                .builder()
                .newAccessToken(jwtUtils.generateAccessToken(user))
                .refreshToken(refreshToken)
                .role(user.getRole().toString())
                .userId(user.getId())
                .build();
    }
}
