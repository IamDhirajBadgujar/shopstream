package com.shopstream.order_service.DTO;


import java.util.List;

public class CreateOrderRequest {
    public static class Line {
        private String productId;
        private Integer qty;
        private Double price; // optional for client

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public Integer getQty() { return qty; }
        public void setQty(Integer qty) { this.qty = qty; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
    }

    private List<Line> items;
    private String shippingAddress;

    public List<Line> getItems() { return items; }
    public void setItems(List<Line> items) { this.items = items; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
}
