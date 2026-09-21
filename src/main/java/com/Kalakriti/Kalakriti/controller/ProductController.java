package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.dto.ProductResponseDTO;
import com.Kalakriti.Kalakriti.entity.Product;
import com.Kalakriti.Kalakriti.service.ProductService;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;


import java.awt.print.Pageable;
import java.util.List;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }



    @GetMapping("/{id}")
    public ProductResponseDTO getProduct(@PathVariable Long id) {
        return productService.getProductById(id);
    }


    @GetMapping
    public Page<ProductResponseDTO> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search
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
}