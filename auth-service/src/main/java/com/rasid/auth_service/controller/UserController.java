package com.rasid.auth_service.controller;

import com.rasid.auth_service.dto.*;
import com.rasid.auth_service.dto.AuthRequestDto;
import com.rasid.auth_service.service.UserService;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid UserRequestDto request){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid UserRequestDto request){
        return ResponseEntity.ok(service.login());
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDto> refreshToken(@Valid RefreshTokenRequestDto requestDto){
        return ResponseEntity.ok(service.refreshToken(requestDto));
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(){
        service.logout();
        return ResponseEntity.ok().body("Logged out");
    }
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUserDetails(){
        return ResponseEntity.ok(service.getCurrentUserDetails());
    }
    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateUserDetails(UserRequestDto request){
        return ResponseEntity.ok(service.updateUserDetails());
    }
    @PutMapping("/change-password")
    ResponseEntity<String> changeUserPassword(@Valid ChangePasswordRequestDto request){
        return ResponseEntity.ok(service.changeUserPassword()).body("Password was changed successfully");
    }
    @GetMapping("/users")
    @PreAuthorize("HasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAllUser(){
        return ResponseEntity.ok(service.getAllUsers());
    }
    @PutMapping("/users/{id}/role")
    @PreAuthorize("HasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> updateUserRole(@PathVariable UUID id, @Valid NewRoleDto dto) {
        return ResponseEntity.ok(service.updateUserRole(id, dto));
    }

}
