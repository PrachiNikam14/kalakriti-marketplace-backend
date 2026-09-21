package com.Kalakriti.Kalakriti.service;

import com.Kalakriti.Kalakriti.dto.AuthResponse;
import com.Kalakriti.Kalakriti.dto.UserLoginRequest;
import com.Kalakriti.Kalakriti.dto.UserRegisterRequest;
import com.Kalakriti.Kalakriti.dto.UserResponse;
import com.Kalakriti.Kalakriti.entity.PasswordResetToken;
import com.Kalakriti.Kalakriti.entity.Role;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.exception.InvalidCredentialsException;
import com.Kalakriti.Kalakriti.exception.UserAlreadyExistsException;
import com.Kalakriti.Kalakriti.repository.PasswordResetTokenRepository;
import com.Kalakriti.Kalakriti.repository.UserRepository;
import com.Kalakriti.Kalakriti.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailService emailService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }

    // =========================
    // REGISTER USER
    // =========================

    public UserResponse register(UserRegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        if (request.getRole() == Role.ARTISAN) {
            user.setVerificationStatus("PENDING");
        } else {
            user.setVerificationStatus("APPROVED");
        }

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    // =========================
    // LOGIN USER
    // =========================

    public AuthResponse login(UserLoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        ));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        String token =
                jwtService.generateToken(
                        user.getEmail(),
                        user.getRole()
                );

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        return new AuthResponse(token, userResponse);
    }

    // =========================
    // FORGOT PASSWORD
    // =========================

    public void forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No account found with this email"
                        ));

        // Delete previous reset tokens
        passwordResetTokenRepository.deleteByUser(user);

        // Generate unique reset token
        String token = UUID.randomUUID().toString();

        // Token expires after 15 minutes
        LocalDateTime expiryDate =
                LocalDateTime.now().plusMinutes(15);

        PasswordResetToken resetToken =
                new PasswordResetToken(
                        token,
                        user,
                        expiryDate
                );

        passwordResetTokenRepository.save(resetToken);

        // Frontend reset page
        String resetLink =
                "http://localhost:5173/reset-password?token="
                        + token;

        String subject =
                "Kalakriti - Reset Your Password";

        String body =
                "Hello " + user.getName() + ",\n\n" +

                        "We received a request to reset your " +
                        "Kalakriti account password.\n\n" +

                        "Click the link below to reset your password:\n\n" +

                        resetLink + "\n\n" +

                        "This link will expire in 15 minutes.\n\n" +

                        "If you did not request a password reset, " +
                        "please ignore this email.\n\n" +

                        "Regards,\n" +
                        "Kalakriti Team";

        // Use existing EmailService
        emailService.sendEmail(
                user.getEmail(),
                subject,
                body
        );
    }

    // =========================
    // RESET PASSWORD
    // =========================

    public void resetPassword(
            String token,
            String newPassword) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid reset token"
                                ));

        // Check token expiry
        if (resetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            passwordResetTokenRepository.delete(resetToken);

            throw new RuntimeException(
                    "Reset token has expired"
            );
        }

        User user = resetToken.getUser();

        // Encrypt the new password
        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);

        // Delete token after successful reset
        passwordResetTokenRepository.delete(resetToken);
    }
}