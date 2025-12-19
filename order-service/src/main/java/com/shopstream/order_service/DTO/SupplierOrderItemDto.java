package com.shopstream.order_service.dto;

import java.math.BigDecimal;

public class SupplierOrderItemDto {

    private Long orderId;
    private String buyerName;
    private String address;
    private String productId;
    private String productName;
    private Integer qty;
    private BigDecimal price;

    public SupplierOrderItemDto(
        Long orderId,
        String buyerName,
        String address,
        String productId,
        String productName,
        Integer qty,
        BigDecimal price
    ) {
        this.orderId = orderId;
        this.buyerName = buyerName;
        this.address = address;
        this.productId = productId;
        this.productName = productName;
        this.qty = qty;
        this.price = price;
    }

	public SupplierOrderItemDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
