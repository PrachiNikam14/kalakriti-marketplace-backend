package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.dto.CartResponseDTO;
import com.Kalakriti.Kalakriti.dto.CheckoutResponseDTO;
import com.Kalakriti.Kalakriti.entity.Cart;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.service.CartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public String addToCart(@AuthenticationPrincipal User user,
                            @RequestParam Long productId,
                            @RequestParam int quantity) {
        return cartService.addToCart(user, productId, quantity);
    }

    @GetMapping
    public CartResponseDTO viewCart(@AuthenticationPrincipal User user) {
        return cartService.viewCart(user);
    }

    @DeleteMapping("/{cartItemId}")
    public String removeItem(@AuthenticationPrincipal User user,
                             @PathVariable Long cartItemId) {
        return cartService.removeItem(user, cartItemId);
    }

    @PostMapping("/checkout")
    public CheckoutResponseDTO checkout(@AuthenticationPrincipal User user) {
        return cartService.checkout(user);
    }


    @PutMapping("/{cartItemId}")
    public String updateQuantity(
            @AuthenticationPrincipal User user,
            @PathVariable Long cartItemId,
            @RequestParam int quantity
    ) {
        return cartService.updateQuantity(
                user,
                cartItemId,
                quantity
        );
    }
}