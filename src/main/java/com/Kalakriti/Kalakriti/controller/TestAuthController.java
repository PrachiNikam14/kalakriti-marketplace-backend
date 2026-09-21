package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestAuthController {

    @GetMapping("/me")
    public String me(@AuthenticationPrincipal User user) {

        if (user == null) {
            return "USER IS NULL";
        }

        return user.getEmail() + " | " + user.getRole();
    }
}