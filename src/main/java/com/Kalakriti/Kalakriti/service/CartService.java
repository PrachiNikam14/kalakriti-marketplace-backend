package com.Kalakriti.Kalakriti.service;

import com.Kalakriti.Kalakriti.dto.CartItemResponseDTO;
import com.Kalakriti.Kalakriti.dto.CartResponseDTO;
import com.Kalakriti.Kalakriti.dto.CheckoutResponseDTO;
import com.Kalakriti.Kalakriti.entity.*;
import com.Kalakriti.Kalakriti.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Kalakriti.Kalakriti.entity.ApprovalStatus;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       OrderRepository orderRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }
    // 🟢 Add to Cart
    public String addToCart(User user, Long productId, int quantity) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getApprovalStatus() != ApprovalStatus.APPROVED || !product.isActive()) {
            throw new RuntimeException("Product not available");
        }

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });

        // Check if item already exists
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                cartItemRepository.save(item);
                return "Quantity updated in cart";
            }
        }

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);
        cart.getItems().add(cartItem);

        return "Product added to cart";
    }

    // 🟢 View Cart
    // 🟢 View Cart
    public CartResponseDTO viewCart(User user) {

        Cart cart = cartRepository.findByUser(user).orElse(null);

        // User has not created a cart yet
        if (cart == null) {
            return new CartResponseDTO(
                    null,
                    new ArrayList<>(),
                    0
            );
        }

        List<CartItem> cartItems = cart.getItems();

        // Safety check in case the items collection is null
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }

        List<CartItemResponseDTO> itemDTOs = cartItems.stream()
                .map(item -> {

                    double totalPrice =
                            item.getProduct().getPrice() * item.getQuantity();

                    List<String> urls = item.getProduct()
                            .getImageUrls()
                            .stream()
                            .map(ProductImageUrl::getImageUrl)
                            .toList();

                    String imageUrl =
                            !urls.isEmpty() ? urls.get(0) : null;

                    return new CartItemResponseDTO(
                            item.getId(),
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            item.getProduct().getPrice(),
                            item.getQuantity(),
                            totalPrice,
                            imageUrl
                    );
                })
                .toList();

        double grandTotal = itemDTOs.stream()
                .mapToDouble(CartItemResponseDTO::getTotalPrice)
                .sum();

        return new CartResponseDTO(
                cart.getId(),
                itemDTOs,
                grandTotal
        );
    }

    // 🟢 Remove item
    public String removeItem(User user, Long cartItemId) {

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Unauthorized action");
        }

        cartItemRepository.delete(item);

        return "Item removed from cart";
    }
    @Transactional
    public CheckoutResponseDTO checkout(User user) {

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart is empty"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double totalAmount = 0;
        List<Long> orderIds = new java.util.ArrayList<>();

        for (CartItem item : cart.getItems()) {

            Product product = item.getProduct();

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // Reduce stock
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            double orderTotal = product.getPrice() * item.getQuantity();
            totalAmount += orderTotal;

            // Create Order
            Order order = new Order();
            order.setUser(user);
            order.setTotalPrice(orderTotal);

            Order savedOrder = orderRepository.save(order);
            orderIds.add(savedOrder.getId());
        }

        int ordersCreated = orderIds.size();

        // Clear cart
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cartRepository.save(cart);

        return new CheckoutResponseDTO(
                "Checkout successful",
                ordersCreated,
                totalAmount,
                orderIds
        );
    }

    public String updateQuantity(
            User user,
            Long cartItemId,
            int quantity
    ) {

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Unauthorized action");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return "Item removed";
        }

        Product product = item.getProduct();

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        item.setQuantity(quantity);

        cartItemRepository.save(item);

        return "Quantity updated";
    }
}