package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.dto.ChangePasswordRequest;
import com.Kalakriti.Kalakriti.dto.UserProfileResponse;
import com.Kalakriti.Kalakriti.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Kalakriti.Kalakriti.dto.UpdateProfileRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public UserProfileResponse getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        return userService.getProfile(userDetails.getUsername());
    }

    @PutMapping("/profile")
    public UserProfileResponse updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {

        return userService.updateProfile(
                userDetails.getUsername(),
                request
        );
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(
                userDetails.getUsername(),
                request
        );

        return ResponseEntity.ok(
                "Password changed successfully"
        );
    }
}