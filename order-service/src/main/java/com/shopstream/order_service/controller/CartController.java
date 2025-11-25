package com.shopstream.order_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopstream.order_service.entity.Cart;
import com.shopstream.order_service.security.CustomUserPrincipal;
import com.shopstream.order_service.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

  @Autowired CartService cartService;

  // assume userId comes from authenticated principal (not from client!)
  @GetMapping
  public Cart getCart(@AuthenticationPrincipal CustomUserPrincipal principal) {
	  System.out.println("GET /api/cart  resolved userId=" + (principal.getUserId()) + " token=" );

    return cartService.getOrCreateCart(principal.getUserId());
  }

//  @PostMapping("/items")
//  public Cart addItem(@AuthenticationPrincipal CustomUserPrincipal principal,
//                      @RequestBody AddItemRequest req) {
//    return cartService.addItem(principal.getUserId(), req.productId, req.quantity);
//  }

  @DeleteMapping("/items/{productId}")
  public Cart removeItem(@AuthenticationPrincipal CustomUserPrincipal principal,
                         @PathVariable Long productId) {
    return cartService.removeItem(principal.getUserId(), productId);
  }
}
