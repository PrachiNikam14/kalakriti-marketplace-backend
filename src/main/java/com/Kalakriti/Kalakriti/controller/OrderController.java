package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.dto.OrderResponseDTO;
import com.Kalakriti.Kalakriti.dto.OrderStatusUpdateResponseDTO;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/user/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderResponseDTO> getUserOrders(@AuthenticationPrincipal User user) {
        return orderService.getUserOrders(user);
    }

    @PutMapping("/{orderId}/cancel")
    public OrderStatusUpdateResponseDTO cancelOrder(
            @AuthenticationPrincipal User user,
            @PathVariable Long orderId) {

        return orderService.cancelOrder(user, orderId);
    }

    @PostMapping("/checkout")
    public String checkout(@AuthenticationPrincipal User user,
                           @RequestParam Long addressId) {

        return orderService.checkout(user, addressId);
    }

    @GetMapping("/test")
    public String test() {
        return "OrderController working";
    }

    @PostMapping("/{orderId}/pay")
    public String initiatePayment(@PathVariable Long orderId) throws Exception {
        return orderService.initiatePayment(orderId);
    }

    @PostMapping("/verify-payment")
    public String verifyPayment(@RequestBody Map<String, String> request) throws Exception {

        String razorpayOrderId = request.get("razorpay_order_id");
        String razorpayPaymentId = request.get("razorpay_payment_id");
        String razorpaySignature = request.get("razorpay_signature");

        return orderService.verifyPayment(
                razorpayOrderId,
                razorpayPaymentId,
                razorpaySignature
        );
    }
}


