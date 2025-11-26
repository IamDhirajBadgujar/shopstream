package com.shopstream.order_service.dto;



public class AddToCartRequest {
 private Long userId;
 private String productId;
 private int quantity;

 public Long getUserId() { return userId; }
 public void setUserId(Long userId) { this.userId = userId; }

 public String getProductId() { return productId; }
 public void setProductId(String productId) { this.productId = productId; }

 public int getQuantity() { return quantity; }
 public void setQuantity(int quantity) { this.quantity = quantity; }
}
