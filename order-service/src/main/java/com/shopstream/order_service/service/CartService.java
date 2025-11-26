package com.shopstream.order_service.service;

import com.shopstream.order_service.InventoryClient;
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
    private final InventoryClient inventoryClient;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       InventoryClient inventoryClient) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.inventoryClient = inventoryClient;
    }

    // ---------- existing methods ----------

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

    @Transactional(readOnly = true)
    public Optional<Cart> getCart(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    /**
     * Add quantity of a product to user's cart.
     * Enforces:
     *  - cannot add if product stock <= 0
     *  - cannot add more than available stock (existing + new > stock)
     */
    public Cart addToCart(Long userId, String string, int qtyToAdd) {
        if (qtyToAdd <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        // 1️⃣ Get product info (stock, price, etc.) from inventory-service
        InventoryClient.ProductDto product = inventoryClient.getProduct(string);
        if (product == null) {
            throw new RuntimeException("Product not found in inventory");
        }

        int stock = product.getStock() != null ? product.getStock() : 0;

        // 2️⃣ If product is out of stock
        if (stock <= 0) {
            throw new RuntimeException("Product is out of stock");
        }

        // 3️⃣ Load or create cart for user
        Cart cart = getOrCreateCart(userId);

        // 4️⃣ Find existing CartItem for this product
        CartItem existing = cart.getItems().stream()
                .filter(i -> Objects.equals(i.getProductId(), string))
                .findFirst()
                .orElse(null);

        int existingQty = existing != null ? existing.getQuantity() : 0;
        int requestedTotal = existingQty + qtyToAdd;

        // 5️⃣ Enforce "cannot exceed stock"
        if (requestedTotal > stock) {
            throw new RuntimeException(
                    "Cannot add more than available stock (" + stock + "). Already in cart: " + existingQty
            );
        }

        // 6️⃣ Create or update CartItem
        if (existing == null) {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProductId(string);
            item.setQuantity(qtyToAdd);
            item.setAddedAt(new Date());

            // optional: snapshot name/price in cart if you have fields in CartItem
            // item.setProductName(product.getName());
            // item.setPriceAtTime(product.getPrice());

            cart.getItems().add(item);
            cartItemRepository.save(item);
        } else {
            existing.setQuantity(requestedTotal);
            cartItemRepository.save(existing);
        }

        // return latest cart from DB
        return cartRepository.findById(cart.getId()).orElse(cart);
    }

    // ---------- your existing methods (unchanged) ----------

    public Cart updateItemQuantity(Long userId, String productId, int quantity) {
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
            if (quantity > 0) {
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

    public void clearCart(Long userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cartItemRepository.deleteAll(cart.getItems());
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    public Cart mergeGuestCart(Long userId, List<CartItemDTO> guestItems) {
        if (guestItems == null || guestItems.isEmpty()) {
            return getOrCreateCart(userId);
        }

        Cart cart = getOrCreateCart(userId);

        Map<String, CartItem> existingByProduct = new HashMap<>();
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
