package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.dto.AuthResponse;
import com.Kalakriti.Kalakriti.dto.UserLoginRequest;
import com.Kalakriti.Kalakriti.dto.UserRegisterRequest;
import com.Kalakriti.Kalakriti.dto.UserResponse;


import com.Kalakriti.Kalakriti.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.Kalakriti.Kalakriti.dto.ForgotPasswordRequest;
import com.Kalakriti.Kalakriti.dto.ResetPasswordRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody UserRegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody UserLoginRequest request
    ) {
        return authService.login(request);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request.getEmail());

        return ResponseEntity.ok(
                "Password reset link has been sent to your email"
        );
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                "Password has been reset successfully"
        );
    }
}