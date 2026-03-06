package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.entity.ApprovalStatus;
import com.Kalakriti.Kalakriti.entity.Product;
import com.Kalakriti.Kalakriti.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminProductController {

    private final ProductRepository productRepository;

    public AdminProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PutMapping("/approve-product/{id}")
    public String approveProduct(@PathVariable Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setApprovalStatus(ApprovalStatus.valueOf("APPROVED"));
        productRepository.save(product);

        return "Product approved successfully";
    }
}