package com.Kalakriti.Kalakriti.repository;

import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Optional<Wishlist> findByUser(User user);
}