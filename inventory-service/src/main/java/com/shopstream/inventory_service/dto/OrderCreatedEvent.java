package com.shopstream.inventory_service.dto;

import java.util.List;

public class OrderCreatedEvent {
    private Long orderId;
    private List<OrderLine> items;

    public static class OrderLine {
        private String productId;
        private Integer qty;
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public Integer getQty() { return qty; }
        public void setQty(Integer qty) { this.qty = qty; }
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public List<OrderLine> getItems() { return items; }
    public void setItems(List<OrderLine> items) { this.items = items; }
}
