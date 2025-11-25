package com.shopstream.order_service.service;


import com.shopstream.order_service.dto.CartItemDTO;
import com.shopstream.order_service.entity.Cart;
import com.shopstream.order_service.entity.CartItem;
import com.shopstream.order_service.repository.CartItemRepository;
import com.shopstream.order_service.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * Fetches the cart for a given userId or creates a new one if not present.
     */
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUserId(userId);
                    c.setCreatedAt(new Date());
                    c.setItems(new ArrayList<>());
                    return cartRepository.save(c);
                });
    }

    /**
     * Returns current cart (null if not exists). Prefer getOrCreateCart when you want to ensure one exists.
     */
    @Transactional(readOnly = true)
    public Optional<Cart> getCart(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    /**
     * Add quantity of a product to user's cart. If item exists, increments quantity.
     */
    public Cart addItem(Long userId, Long productId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");

        Cart cart = getOrCreateCart(userId);

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> Objects.equals(i.getProductId(), productId))
                .findFirst();

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProductId(productId);
            item.setQuantity(quantity);
            item.setAddedAt(new Date());
            cart.getItems().add(item);
            cartItemRepository.save(item);
        }

        // ensure the cart is refreshed and returned
        return cartRepository.findById(cart.getId()).orElse(cart);
    }

    /**
     * Update quantity for a product in the cart. If quantity <= 0, the item will be removed.
     */
    public Cart updateItemQuantity(Long userId, Long productId, int quantity) {
        Cart cart = getOrCreateCart(userId);

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> Objects.equals(i.getProductId(), productId))
                .findFirst();

        if (existing.isPresent()) {
            CartItem item = existing.get();
            if (quantity <= 0) {
                cart.getItems().remove(item);
                cartItemRepository.delete(item);
            } else {
                item.setQuantity(quantity);
                cartItemRepository.save(item);
            }
        } else {
            if (quantity > 0) { // create new if not exists and qty > 0
                CartItem item = new CartItem();
                item.setCart(cart);
                item.setProductId(productId);
                item.setQuantity(quantity);
                item.setAddedAt(new Date());
                cart.getItems().add(item);
                cartItemRepository.save(item);
            }
        }

        return cartRepository.findById(cart.getId()).orElse(cart);
    }

    /**
     * Remove a product from user's cart.
     */
    public Cart removeItem(Long userId, Long productId) {
        Cart cart = getOrCreateCart(userId);

        List<CartItem> toRemove = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            if (Objects.equals(item.getProductId(), productId)) {
                toRemove.add(item);
            }
        }

        if (!toRemove.isEmpty()) {
            cart.getItems().removeAll(toRemove);
            cartItemRepository.deleteAll(toRemove);
        }

        return cartRepository.findById(cart.getId()).orElse(cart);
    }

    /**
     * Clear whole cart for a user (used after successful checkout).
     */
    public void clearCart(Long userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cartItemRepository.deleteAll(cart.getItems());
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    /**
     * Merge guest items into user's cart. If an item exists, sums quantities.
     *
     * guestItems: List of pairs (productId, quantity) coming from frontend guest cart.
     */
    public Cart mergeGuestCart(Long userId, List<CartItemDTO> guestItems) {
        if (guestItems == null || guestItems.isEmpty()) {
            return getOrCreateCart(userId);
        }

        Cart cart = getOrCreateCart(userId);

        // Map existing items for quick lookup
        Map<Long, CartItem> existingByProduct = new HashMap<>();
        for (CartItem ci : cart.getItems()) {
            existingByProduct.put(ci.getProductId(), ci);
        }

        for (CartItemDTO dto : guestItems) {
            if (dto.getQuantity() <= 0) continue;
            CartItem existing = existingByProduct.get(dto.getProductId());
            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + dto.getQuantity());
                cartItemRepository.save(existing);
            } else {
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setProductId(dto.getProductId());
                newItem.setQuantity(dto.getQuantity());
                newItem.setAddedAt(new Date());
                cart.getItems().add(newItem);
                cartItemRepository.save(newItem);
            }
        }

        return cartRepository.findById(cart.getId()).orElse(cart);
    }

      
}

