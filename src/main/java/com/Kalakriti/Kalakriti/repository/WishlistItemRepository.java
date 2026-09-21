package com.Kalakriti.Kalakriti.repository;

import com.Kalakriti.Kalakriti.entity.Product;
import com.Kalakriti.Kalakriti.entity.Wishlist;
import com.Kalakriti.Kalakriti.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistItemRepository
        extends JpaRepository<WishlistItem, Long> {

    Optional<WishlistItem> findByWishlistAndProduct(
            Wishlist wishlist,
            Product product
    );
}
