package com.shopstream.order_service.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopstream.order_service.dto.AddToCartRequest;
import com.shopstream.order_service.dto.MergeCartRequest;
import com.shopstream.order_service.dto.UpdateQtyRequest;
import com.shopstream.order_service.entity.Cart;
import com.shopstream.order_service.security.CustomUserPrincipal;
import com.shopstream.order_service.service.CartService;
import com.shopstream.order_service.service.UserService;


@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:4200")
public class CartController {

  @Autowired CartService cartService;

  @Autowired UserService userService;

  

  // assume userId comes from authenticated principal (not from client!)
  @GetMapping
  public Cart getCart(@AuthenticationPrincipal CustomUserPrincipal principal) {
	  System.out.println("GET /api/cart  resolved" );
	  System.out.println("GET /api/cart  resolved userId=" + (principal.getUserId()) + " token=" );

    return cartService.getOrCreateCart(principal.getUserId());
  }

  @DeleteMapping("/items/{productId}")
  public Cart removeItem(@AuthenticationPrincipal CustomUserPrincipal principal,
                         @PathVariable Long productId) {
    return cartService.removeItem(principal.getUserId(), productId);
  }
  
  // ---------- add item to cart ----------
  @PostMapping("/items")
  public ResponseEntity<?> addItem(@RequestBody AddToCartRequest req, Principal principal) {
      try {
          Long userId = userService.getUserIdFromPrincipal(principal); // decode from JWT / DB
          Cart cart = cartService.addToCart(userId, req.getProductId(), req.getQuantity());
          return ResponseEntity.ok(cart);
      } catch (RuntimeException  ex) {
          return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
      }
  }


  // ---------- merge guest cart ----------
//  @PostMapping("/merge")
//  public ResponseEntity<?> mergeGuestCart(@RequestBody MergeCartRequest req) {
//      try {
//          Cart cart = cartService.mergeGuestCart(req.getUserId(), req.getItems());
//          return ResponseEntity.ok(cart);
//      } catch (RuntimeException ex) {
//          return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
//      }
//  }
  
  @PutMapping("/items/{productId}")
  public Cart updateItemQuantity(
          @AuthenticationPrincipal CustomUserPrincipal principal,
          @PathVariable String productId,
          @RequestBody UpdateQtyRequest req) {

      System.out.println("PUT /api/cart/items " +
              "principal=" + principal +
              ", productId=" + productId +
              ", qty=" + req.getQuantity());

      if (principal == null || principal.getUserId() == null) {
          throw new IllegalStateException("User not authenticated or userId missing in principal");
      }

      Long userId = principal.getUserId();
      int qty = req.getQuantity();

      return cartService.updateItemQuantity(userId, productId, qty);
  }


  
  
}



