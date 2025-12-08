// OrderDetails.java
package com.shopstream.order_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDetails {

    private Long orderId;
    private Long userId;
    private String shippingAddress;
    private BigDecimal total;
    private LocalDateTime createdAt;
    private List<OrderItemDetails> items;
 
    // optional: status, paymentMode, etc.
    // private String status;

   
	public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<OrderItemDetails> getItems() { return items; }
    public void setItems(List<OrderItemDetails> items) { this.items = items; }

    // nested item DTO
    public static class OrderItemDetails {
        private Long id;
        private String productId;
        private Integer qty;
        private BigDecimal price;
        private String productName; 
        // optional: productName, thumbnailUrl (from inventory if you want)

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }

        public Integer getQty() { return qty; }
        public void setQty(Integer qty) { this.qty = qty; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
        
    }

	
}
