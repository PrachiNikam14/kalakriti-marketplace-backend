package com.Kalakriti.Kalakriti.service;

import com.Kalakriti.Kalakriti.dto.ProductResponseDTO;
import com.Kalakriti.Kalakriti.entity.Product;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.entity.Review;
import com.Kalakriti.Kalakriti.repository.ProductRepository;
import com.Kalakriti.Kalakriti.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;



import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    public ProductService(ProductRepository productRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    public ProductResponseDTO addProduct(Product product, User artisan) {

        if(!artisan.getVerificationStatus().equals("APPROVED")) {
            throw new RuntimeException("Artisan not verified yet");
        }

        product.setArtisan(artisan);
        Product saved = productRepository.save(product);

        return convertToDTO(saved);
    }


    public Page<ProductResponseDTO> getAllProducts(
            int page,
            int size,
            String sortBy,
            String direction,
            String category,
            String search
    ) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage =
                productRepository
                        .findByApprovalStatusAndActiveAndCategoryContainingIgnoreCaseAndNameContainingIgnoreCase(
                                "APPROVED",
                                true,
                                category,
                                search,
                                pageable
                        );

        return productPage.map(this::convertToDTO);
    }


    public List<ProductResponseDTO> getProductsByArtisan(User artisan) {

        return productRepository.findByArtisan(artisan)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public ProductResponseDTO getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return convertToDTO(product);
    }

    public ProductResponseDTO convertToDTO(Product product) {

        List<Review> reviews = reviewRepository.findByProduct(product);

        double averageRating = 0;
        long reviewCount = reviews.size();

        if (reviewCount > 0) {
            averageRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0);
        }

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getImageUrl(),
                product.getCreatedAt(),
                product.getArtisan().getId(),
                product.getArtisan().getName(),
                product.getArtisan().getEmail(),
                averageRating,
                reviewCount
        );
    }
}