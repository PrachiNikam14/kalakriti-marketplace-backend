package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/health")
    public String health() {
        return "Kalakriti Backend is running 🚀";
    }

    @RestController
    @RequestMapping("/test")
    public class TestEmailController {

        private final EmailService emailService;

        public TestEmailController(EmailService emailService) {
            this.emailService = emailService;
        }

        @GetMapping("/email")
        public String testEmail() {
            emailService.sendEmail(
                    "prachi.nikam24@aiml.sce.edu.in",
                    "Test Email",
                    "This is a test email from Kalakriti"
            );
            return "Email triggered";
        }
    }
}
