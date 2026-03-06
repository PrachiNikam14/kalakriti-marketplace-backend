package com.Kalakriti.Kalakriti.repository;

import com.Kalakriti.Kalakriti.entity.Product;
import com.Kalakriti.Kalakriti.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Pagination + Filtering (public listing)
    Page<Product> findByApprovalStatusAndActiveAndCategoryContainingIgnoreCaseAndNameContainingIgnoreCase(
            String approvalStatus,
            boolean active,
            String category,
            String name,
            Pageable pageable
    );

    // Artisan-specific products
    List<Product> findByArtisan(User artisan);
}