package com.Kalakriti.Kalakriti.service;

import com.Kalakriti.Kalakriti.dto.ProductResponseDTO;
import com.Kalakriti.Kalakriti.entity.*;
import com.Kalakriti.Kalakriti.repository.ProductRepository;
import com.Kalakriti.Kalakriti.repository.ReviewRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    public ProductService(ProductRepository productRepository,
                          ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    // ADD PRODUCT (ARTISAN)
    public ProductResponseDTO addProduct(Product product, User artisan) {
        if (!"APPROVED".equalsIgnoreCase(artisan.getVerificationStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Artisan not verified yet");
        }

        product.setArtisan(artisan);
        product.setApprovalStatus(ApprovalStatus.PENDING);

        Product saved = productRepository.save(product);
        return convertToDTO(saved);
    }

    // GET ALL PRODUCTS WITH PAGINATION + FILTER + SEARCH + SORT
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

        Page<Product> productPage;

        if (category != null && !category.isBlank()) {
            productPage = productRepository
                    .findByApprovalStatusAndActiveTrueAndCategoryAndNameContainingIgnoreCase(
                            ApprovalStatus.APPROVED,
                            CategoryType.valueOf(category.toUpperCase()),
                            search == null ? "" : search,
                            pageable
                    );
        } else {
            productPage = productRepository
                    .findByApprovalStatusAndActiveTrueAndNameContainingIgnoreCase(
                            ApprovalStatus.APPROVED,
                            search == null ? "" : search,
                            pageable
                    );
        }

        return productPage.map(this::convertToDTO);
    }

    // GET PRODUCTS BY ARTISAN
    public List<ProductResponseDTO> getProductsByArtisan(User artisan) {
        return productRepository.findByArtisan(artisan)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // GET PRODUCT BY ID
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return convertToDTO(product);
    }

    // CONVERT ENTITY → DTO
    private ProductResponseDTO convertToDTO(Product product) {
        // Reviews
        List<Review> reviews = reviewRepository.findByProduct(product);
        double averageRating = 0;
        long reviewCount = reviews.size();

        if (reviewCount > 0) {
            averageRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0);
        }

        // Safely convert image URLs
        List<String> urls = product.getImageUrls() != null
                ? product.getImageUrls().stream()
                .map(ProductImageUrl::getImageUrl)
                .collect(Collectors.toList())
                : List.of();

        String firstImage = (!urls.isEmpty()) ? urls.get(0) : null;

        // Safely map artisan
        Long artisanId = (product.getArtisan() != null) ? product.getArtisan().getId() : null;
        String artisanName = (product.getArtisan() != null) ? product.getArtisan().getName() : null;
        String artisanEmail = (product.getArtisan() != null) ? product.getArtisan().getEmail() : null;

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory().name(),
                firstImage,
                urls,
                product.getCreatedAt(),
                artisanId,
                artisanName,
                artisanEmail,
                averageRating,
                reviewCount,
                product.getStockQuantity()

        );
    }

    // UPDATE PRODUCT
    public ProductResponseDTO updateProduct(Product product) {
        Product saved = productRepository.save(product);
        return convertToDTO(saved);
    }
}
