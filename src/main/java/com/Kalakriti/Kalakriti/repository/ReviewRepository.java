package com.Kalakriti.Kalakriti.repository;

import com.Kalakriti.Kalakriti.entity.Product;
import com.Kalakriti.Kalakriti.entity.Review;
import com.Kalakriti.Kalakriti.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProduct(Product product);

    Optional<Review> findByUserAndProduct(User user, Product product);
}