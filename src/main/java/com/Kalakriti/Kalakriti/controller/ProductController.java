package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.dto.ProductResponseDTO;
import com.Kalakriti.Kalakriti.entity.Product;
import com.Kalakriti.Kalakriti.service.ProductService;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;



import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping
    public Page<ProductResponseDTO> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "") String search
    ) {

        return productService.getAllProducts(
                page,
                size,
                sortBy,
                direction,
                category,
                search
        );
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getProduct(@PathVariable Long id) {
        return productService.getProductById(id);
    }
}