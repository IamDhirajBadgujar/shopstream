package com.shopstream.order_service.dto;

public class CartItemDTO {
    private String productId;
    private int quantity;

    public CartItemDTO() {}

    public CartItemDTO(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}