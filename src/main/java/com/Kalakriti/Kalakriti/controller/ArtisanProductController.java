package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.dto.ProductResponseDTO;
import com.Kalakriti.Kalakriti.entity.ApprovalStatus;
import com.Kalakriti.Kalakriti.entity.Product;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.service.ProductService;
import com.Kalakriti.Kalakriti.repository.ProductRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/artisan/products")
public class ArtisanProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;



    public ArtisanProductController(ProductService productService,
                                    ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @PostMapping
    public ProductResponseDTO addProduct(@RequestBody Product product,
                                         @AuthenticationPrincipal User artisan) {

        return productService.addProduct(product, artisan);
    }

    @GetMapping
    public List<ProductResponseDTO> getMyProducts(
            @AuthenticationPrincipal User artisan) {

        return productService.getProductsByArtisan(artisan);
    }

    @PutMapping("/{id}")
    public ProductResponseDTO updateProduct(
            @PathVariable Long id,
            @RequestBody Product updatedProduct,
            @AuthenticationPrincipal User artisan) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getArtisan().getId().equals(artisan.getId())) {
            throw new RuntimeException("You can only update your own product");
        }

        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setCategory(updatedProduct.getCategory());
        product.setStockQuantity(updatedProduct.getStockQuantity());

        // Reset approval when edited
        product.setApprovalStatus(ApprovalStatus.PENDING);

        return productService.updateProduct(product);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal User artisan) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getArtisan().getId().equals(artisan.getId())) {
            throw new RuntimeException("You can only delete your own product");
        }

        product.setActive(false);
        productRepository.save(product);

        return "Product deleted successfully";
    }
}
