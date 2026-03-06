package com.Kalakriti.Kalakriti.service;

import com.Kalakriti.Kalakriti.dto.AuthResponse;
import com.Kalakriti.Kalakriti.dto.UserLoginRequest;
import com.Kalakriti.Kalakriti.dto.UserRegisterRequest;
import com.Kalakriti.Kalakriti.dto.UserResponse;
import com.Kalakriti.Kalakriti.entity.Role;
import com.Kalakriti.Kalakriti.exception.InvalidCredentialsException;
import com.Kalakriti.Kalakriti.exception.UserAlreadyExistsException;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.repository.UserRepository;
import com.Kalakriti.Kalakriti.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;



    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }



    // REGISTER USER
    public UserResponse register(UserRegisterRequest request) {

        // 1. Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered");

        }

        // 2. Create User entity
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ARTISAN);

        // 3. Save to DB
        User savedUser = userRepository.save(user);

        // 4. Return response DTO (no password)
        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    // LOGIN USER
    public AuthResponse login(UserLoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        return new AuthResponse(token, userResponse);
    }

}
