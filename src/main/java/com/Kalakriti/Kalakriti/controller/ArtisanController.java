package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.dto.ArtisanOrderDTO;
import com.Kalakriti.Kalakriti.entity.OrderItem;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.service.OrderService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/artisan")
public class ArtisanController {

    private final OrderService orderService;

    public ArtisanController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<ArtisanOrderDTO>> getArtisanOrders(
            Authentication authentication,
            Pageable pageable) {

        User artisan = (User) authentication.getPrincipal();

        Page<ArtisanOrderDTO> orders =
                orderService.getOrdersForArtisan(artisan.getId(), pageable);

        return ResponseEntity.ok(orders);
    }
}