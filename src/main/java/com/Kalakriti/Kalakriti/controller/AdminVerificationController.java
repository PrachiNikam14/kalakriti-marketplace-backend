package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.repository.UserRepository;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminVerificationController {

    private final UserRepository userRepository;

    public AdminVerificationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PutMapping("/approve-artisan/{id}")
    public String approveArtisan(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!user.getRole().equals("ARTISAN")) {
            return "User is not an artisan";
        }

        user.setVerificationStatus("APPROVED");
        userRepository.save(user);

        return "Artisan approved successfully";
    }
}