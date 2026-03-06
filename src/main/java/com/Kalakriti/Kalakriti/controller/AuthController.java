package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.dto.UserResponse;
import com.Kalakriti.Kalakriti.dto.AuthResponse;
import com.Kalakriti.Kalakriti.dto.UserLoginRequest;
import com.Kalakriti.Kalakriti.security.JwtService;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.repository.UserRepository;

import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {

        if(userRepository.existsByEmail(user.getEmail())) {
            return "Email already exists";
        }
        System.out.println("Password from request: " + user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if(user.getRole().equals("ARTISAN")) {
            user.setVerificationStatus("PENDING");
        } else {
            user.setVerificationStatus("APPROVED"); // normal users auto approved
        }

        userRepository.save(user);

        return "User registered successfully";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody UserLoginRequest request) {

        // 1️⃣ Authenticate credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2️⃣ Fetch real user from DB
        User dbUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3️⃣ Generate token using DB role
        String token = jwtService.generateToken(
                dbUser.getEmail(),
                dbUser.getRole()
        );

        // 4️⃣ Prepare UserResponse DTO
        UserResponse userResponse = new UserResponse(
                dbUser.getId(),
                dbUser.getName(),
                dbUser.getEmail(),
                dbUser.getRole()
        );

        // 5️⃣ Return structured AuthResponse
        return new AuthResponse(token, userResponse);
    }
}