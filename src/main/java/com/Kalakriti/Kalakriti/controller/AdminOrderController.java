package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.dto.OrderStatusUpdateResponseDTO;
import com.Kalakriti.Kalakriti.entity.OrderStatus;
import com.Kalakriti.Kalakriti.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PutMapping("/{orderId}/status")
    public OrderStatusUpdateResponseDTO updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {

        return orderService.updateOrderStatus(orderId, status);
    }
}