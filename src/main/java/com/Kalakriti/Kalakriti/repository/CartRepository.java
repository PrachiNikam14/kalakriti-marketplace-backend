package com.Kalakriti.Kalakriti.repository;

import com.Kalakriti.Kalakriti.entity.Cart;
import com.Kalakriti.Kalakriti.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);
}