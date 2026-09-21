package com.Kalakriti.Kalakriti.service;

import com.Kalakriti.Kalakriti.dto.ReviewRequestDTO;
import com.Kalakriti.Kalakriti.dto.ReviewResponseDTO;
import com.Kalakriti.Kalakriti.entity.Product;
import com.Kalakriti.Kalakriti.entity.Review;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.repository.OrderRepository;
import com.Kalakriti.Kalakriti.repository.ProductRepository;
import com.Kalakriti.Kalakriti.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public ReviewResponseDTO addReview(
            User user,
            Long productId,
            ReviewRequestDTO request
    ) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        if (request.getComment() == null ||
                request.getComment().trim().isEmpty()) {
            throw new RuntimeException("Review comment cannot be empty");
        }

        if (request.getComment().trim().length() < 5) {
            throw new RuntimeException(
                    "Review must contain at least 5 characters"
            );
        }

        if (request.getComment().trim().length() > 500) {
            throw new RuntimeException(
                    "Review cannot contain more than 500 characters"
            );
        }

        boolean hasPurchased =
                orderRepository.hasUserPurchasedProduct(user, productId);

        if (!hasPurchased) {
            throw new RuntimeException(
                    "You can review only products purchased and delivered by you"
            );
        }

        if (reviewRepository.existsByUserAndProduct(user, product)) {
            throw new RuntimeException(
                    "You have already reviewed this product"
            );
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment().trim());

        Review savedReview = reviewRepository.save(review);

        return convertToResponse(savedReview);
    }

    public List<ReviewResponseDTO> getProductReviews(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return reviewRepository
                .findByProductOrderByCreatedAtDesc(product)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private ReviewResponseDTO convertToResponse(Review review) {
        return new ReviewResponseDTO(
                review.getId(),
                review.getUser().getName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}