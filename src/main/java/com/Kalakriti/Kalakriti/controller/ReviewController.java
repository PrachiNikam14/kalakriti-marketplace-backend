package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.dto.ReviewRequestDTO;
import com.Kalakriti.Kalakriti.dto.ReviewResponseDTO;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:5173")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Anyone can view reviews
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponseDTO>> getProductReviews(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                reviewService.getProductReviews(productId)
        );
    }

    // Only logged-in users who purchased the product can add reviews
    @PostMapping("/product/{productId}")
    public ResponseEntity<?> addReview(
            @PathVariable Long productId,
            @RequestBody ReviewRequestDTO request,
            Authentication authentication
    ) {
        try {
            if (authentication == null ||
                    !authentication.isAuthenticated()) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Please login to add a review");
            }

            User user = (User) authentication.getPrincipal();

            ReviewResponseDTO response = reviewService.addReview(
                    user,
                    productId,
                    request
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (RuntimeException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(exception.getMessage());
        }
    }
}