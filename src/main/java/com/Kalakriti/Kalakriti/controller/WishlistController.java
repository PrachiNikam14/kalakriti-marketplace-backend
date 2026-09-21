package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.dto.WishlistItemResponse;
import com.Kalakriti.Kalakriti.entity.WishlistItem;
import com.Kalakriti.Kalakriti.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    // View wishlist
    @GetMapping
    public ResponseEntity<List<WishlistItemResponse>> getWishlist(
                                                                  @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                wishlistService.getWishlist(userDetails.getUsername())
        );
    }

    // Add product to wishlist
    @PostMapping("/{productId}")
    public ResponseEntity<String> addToWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                wishlistService.addToWishlist(
                        userDetails.getUsername(),
                        productId
                )
        );
    }

    // Remove product from wishlist
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeFromWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                wishlistService.removeFromWishlist(
                        userDetails.getUsername(),
                        productId
                )
        );
    }
}