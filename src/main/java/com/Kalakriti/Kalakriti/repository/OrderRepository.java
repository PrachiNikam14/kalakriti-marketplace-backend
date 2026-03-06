package com.Kalakriti.Kalakriti.repository;

import com.Kalakriti.Kalakriti.entity.Order;
import com.Kalakriti.Kalakriti.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);
    @Query("""
    SELECT COUNT(oi) > 0
    FROM Order o
    JOIN o.items oi
    WHERE o.user = :user
    AND oi.product.id = :productId
    AND o.status = com.Kalakriti.Kalakriti.entity.OrderStatus.DELIVERED
    """)
    boolean hasUserPurchasedProduct(User user, Long productId);
}