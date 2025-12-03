package com.shopstream.order_service.dto;


import java.util.List;

public class CreateOrderRequest {
	private Long userId;
	public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public static class Line {
        private String productId;
        private Integer qty;
        private Double price; // optional for client
        private String productName;
        public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public Integer getQty() { return qty; }
        public void setQty(Integer qty) { this.qty = qty; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
		@Override
		public String toString() {
			return "Line [productId=" + productId + ", qty=" + qty + ", price=" + price + ", productName=" + productName
					+ "]";
		}
        
		
    }
    

   
	@Override
	public String toString() {
		return "CreateOrderRequest [userId=" + userId + ", items=" + items + ", shippingAddress=" + shippingAddress
				+ "]";
	}
	private List<Line> items;
    private String shippingAddress;

    public List<Line> getItems() { return items; }
    public void setItems(List<Line> items) { this.items = items; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
}
