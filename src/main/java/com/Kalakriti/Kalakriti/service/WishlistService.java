package com.Kalakriti.Kalakriti.service;

import com.Kalakriti.Kalakriti.dto.ProductWishlistResponse;
import com.Kalakriti.Kalakriti.dto.WishlistItemResponse;
import com.Kalakriti.Kalakriti.entity.Product;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.entity.Wishlist;
import com.Kalakriti.Kalakriti.entity.WishlistItem;
import com.Kalakriti.Kalakriti.repository.ProductRepository;
import com.Kalakriti.Kalakriti.repository.UserRepository;
import com.Kalakriti.Kalakriti.repository.WishlistItemRepository;
import com.Kalakriti.Kalakriti.repository.WishlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistService(
            WishlistRepository wishlistRepository,
            WishlistItemRepository wishlistItemRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    private Wishlist getOrCreateWishlist(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return wishlistRepository.findByUser(user)
                .orElseGet(() -> {
                    Wishlist wishlist = new Wishlist();
                    wishlist.setUser(user);
                    return wishlistRepository.save(wishlist);
                });
    }

    public List<WishlistItemResponse> getWishlist(String email) {

        Wishlist wishlist = getOrCreateWishlist(email);

        return wishlist.getItems()
                .stream()
                .map(item -> {
                    Product product = item.getProduct();

                    String imageUrl = null;

                    if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
                        imageUrl = product.getImageUrls().get(0).getImageUrl();
                    }

                    ProductWishlistResponse productResponse =
                            new ProductWishlistResponse(
                                    product.getId(),
                                    product.getName(),
                                    product.getPrice(),
                                    imageUrl,
                                    product.getStockQuantity()
                            );

                    return new WishlistItemResponse(
                            item.getId(),
                            productResponse
                    );
                })
                .toList();
    }
    public String addToWishlist(String email, Long productId) {

        Wishlist wishlist = getOrCreateWishlist(email);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (wishlistItemRepository
                .findByWishlistAndProduct(wishlist, product)
                .isPresent()) {

            return "Product already exists in wishlist";
        }

        WishlistItem item = new WishlistItem();
        item.setWishlist(wishlist);
        item.setProduct(product);

        wishlist.getItems().add(item);

        wishlistItemRepository.save(item);

        return "Product added to wishlist";
    }

    public String removeFromWishlist(String email, Long productId) {

        Wishlist wishlist = getOrCreateWishlist(email);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        WishlistItem item = wishlistItemRepository
                .findByWishlistAndProduct(wishlist, product)
                .orElseThrow(() -> new RuntimeException("Product not found in wishlist"));

        wishlist.getItems().remove(item);

        wishlistItemRepository.delete(item);

        return "Product removed from wishlist";
    }
}
