package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.dto.*;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.service.ReviewService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{productId}")
    public ReviewResponseDTO addReview(@AuthenticationPrincipal User user,
                                       @PathVariable Long productId,
                                       @RequestBody ReviewRequestDTO request) {

        return reviewService.addReview(user, productId, request);
    }

    @GetMapping("/{productId}")
    public List<ReviewResponseDTO> getReviews(@PathVariable Long productId) {
        return reviewService.getProductReviews(productId);
    }
}