package com.Kalakriti.Kalakriti.service;

import com.Kalakriti.Kalakriti.dto.ReviewRequestDTO;
import com.Kalakriti.Kalakriti.dto.ReviewResponseDTO;
import com.Kalakriti.Kalakriti.entity.*;
import com.Kalakriti.Kalakriti.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         ProductRepository productRepository,
                         OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public ReviewResponseDTO addReview(User user,
                                       Long productId,
                                       ReviewRequestDTO request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 1️⃣ Ensure user purchased the product and order is delivered
        boolean hasPurchased = orderRepository
                .hasUserPurchasedProduct(user, productId);

        if (!hasPurchased) {
            throw new RuntimeException("You can review only delivered products");
        }

        // 2️⃣ Prevent duplicate review
        if (reviewRepository.findByUserAndProduct(user, product).isPresent()) {
            throw new RuntimeException("You have already reviewed this product");
        }

        // 3️⃣ Validate rating
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review saved = reviewRepository.save(review);

        return new ReviewResponseDTO(
                saved.getId(),
                user.getName(),
                saved.getRating(),
                saved.getComment(),
                saved.getCreatedAt()
        );
    }

    public List<ReviewResponseDTO> getProductReviews(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return reviewRepository.findByProduct(product)
                .stream()
                .map(r -> new ReviewResponseDTO(
                        r.getId(),
                        r.getUser().getName(),
                        r.getRating(),
                        r.getComment(),
                        r.getCreatedAt()
                ))
                .toList();
    }
}