package com.rasid.auth_service.service;

import com.rasid.auth_service.dto.*;
import com.rasid.auth_service.entity.RefreshToken;
import com.rasid.auth_service.entity.User;
import com.rasid.auth_service.exception.UserExistsException;
import com.rasid.auth_service.exception.UserNotFoundException;
import com.rasid.auth_service.mapper.UserMapper;
import com.rasid.auth_service.repository.RefreshTokenRepository;
import com.rasid.auth_service.repository.UserRepository;
import com.rasid.auth_service.util.JWTUtils;
import com.rasid.auth_service.util.TokenHasher;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper mapper;
    private final JWTUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final TokenHasher tokenHasher;

    public UserService(UserRepository repo, RefreshTokenRepository refreshTokenRepository, UserMapper mapper, JWTUtils jwtUtils, PasswordEncoder passwordEncoder, TokenHasher tokenHasher) {
        this.userRepo = repo;
        this.refreshTokenRepository = refreshTokenRepository;
        this.mapper = mapper;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.tokenHasher = tokenHasher;
    }

    @Transactional
    public AuthResponseDto register(UserRequestDto request) {
        boolean exists = userRepo.existsByUsernameOrEmail(request.username(), request.email()) > 0;
        if (exists) {
            throw new UserExistsException("User with username: " + request.username() + "or email: " + request.email() + "is already exist");
        }
        User user = mapper.toEntity(request);
        AuthResponseDto response = mapper.toAuthResponse(user);
        RefreshToken refreshToken = mapper.toRefreshTokenEntity(response, user);
        refreshTokenRepository.save(refreshToken);
        return response;
    }

    public AuthResponseDto login(UserLoginRequestDto requestDto) {
        User user = userRepo.findByEmail(requestDto.email()).orElseThrow(() -> new UserNotFoundException("There is no user linked to this email"));
        if (!passwordEncoder.matches(requestDto.password(), user.getPassword())) {
            throw new BadCredentialsException("Authentication error");
        } else {
            AuthResponseDto response = mapper.toAuthResponse(user);
            RefreshToken refreshToken = mapper.toRefreshTokenEntity(response, user);
            refreshTokenRepository.save(refreshToken);
            return response;
        }
    }

    public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto requestDto) {
        boolean validToken = !jwtUtils.isTokenExpired(requestDto.refreshToken())
                && jwtUtils.isTokenValid(requestDto.refreshToken())
                && jwtUtils.isRefreshToken(requestDto.refreshToken());
        if (!validToken) {
            throw new BadCredentialsException("Authentication error");
        }
        RefreshToken storedToken = refreshTokenRepository.findByHashedToken(tokenHasher.hash(requestDto.refreshToken()))
                .orElseThrow(() -> new BadCredentialsException("Authentication error"));
        boolean validStoredToken = storedToken.getExpiresAt().isAfter(LocalDateTime.now());
        if (!validStoredToken) {
            throw new BadCredentialsException("Authentication error");
        }
        User user = userRepo.findById(storedToken.getUser().getId())
                .orElseThrow(() -> new BadCredentialsException("Authentication error"));
        if (!user.isActive()) {
            throw new BadCredentialsException("Authentication error");
        }
        return mapper.toRefreshTokenResponse(user, requestDto.refreshToken());
    }

    @Transactional
    public void logout(RefreshTokenRequestDto requestDto) {
        boolean validToken = !jwtUtils.isTokenExpired(requestDto.refreshToken())
                && jwtUtils.isTokenValid(requestDto.refreshToken())
                && jwtUtils.isRefreshToken(requestDto.refreshToken());
        if (!validToken) {
            throw new BadCredentialsException("Authentication error");
        }
        RefreshToken storedToken = refreshTokenRepository.findByHashedToken(tokenHasher.hash(requestDto.refreshToken()))
                .orElseThrow(() -> new BadCredentialsException("Authentication error"));
        refreshTokenRepository.delete(storedToken);
    }

    public UserResponseDto getCurrentUserDetails() {
        User user = userRepo.findById(getCurrentUserUserId()).orElseThrow(() ->
                new UserNotFoundException("User not found"));
        return mapper.toDto(user);
    }

    @Transactional
    public UserResponseDto updateUserDetails(UserRequestDto requestDto) {
        User user = userRepo.findById(getCurrentUserUserId()).orElseThrow(() ->
                new UserNotFoundException("User not found"));
        if (requestDto.username() != null) {
            if (userRepo.existsByUsername(requestDto.username()))
                throw new UserExistsException("This username has been reserved");
            user.setUsername(requestDto.username());
        }
        if (requestDto.email() != null) {
            if (userRepo.existsByEmail(requestDto.email()))
                throw new UserExistsException("This email has been reserved");
            user.setEmail(requestDto.email());
        }
        if (requestDto.password() != null) {
            user.setPassword(passwordEncoder.encode(requestDto.password()));
        }
        userRepo.save(user);
        return mapper.toDto(user);
    }

    @Transactional
    public void changeUserPassword(ChangePasswordRequestDto requestDto) {
        User user = userRepo.findById(getCurrentUserUserId()).orElseThrow(() ->
                new UserNotFoundException("User not found"));
        if (requestDto.oldPassword() != null && requestDto.newPassword() != null){
            if (passwordEncoder.matches(requestDto.oldPassword(), user.getPassword())){
                user.setPassword(passwordEncoder.encode(requestDto.newPassword()));
            } else throw new BadCredentialsException("Authentication error");
        }else throw new BadCredentialsException("Authentication error");

    }

    private UUID getCurrentUserUserId() {
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
