package com.Kalakriti.Kalakriti.repository;
import com.Kalakriti.Kalakriti.entity.CategoryType;

import com.Kalakriti.Kalakriti.entity.ApprovalStatus;
import com.Kalakriti.Kalakriti.entity.Product;
import com.Kalakriti.Kalakriti.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Pagination + Search + Category filter
    Page<Product> findByApprovalStatusAndActiveTrueAndNameContainingIgnoreCase(
            ApprovalStatus approvalStatus,
            String name,
            Pageable pageable
    );

    // Only approved products (pagination)
    Page<Product> findByApprovalStatusAndActiveTrue(
            ApprovalStatus approvalStatus,
            Pageable pageable
    );

    // Artisan products
    List<Product> findByArtisan(User artisan);

    Page<Product> findByApprovalStatusAndActiveTrueAndCategoryAndNameContainingIgnoreCase(
            ApprovalStatus approvalStatus,
            CategoryType category,
            String name,
            Pageable pageable
    );
}