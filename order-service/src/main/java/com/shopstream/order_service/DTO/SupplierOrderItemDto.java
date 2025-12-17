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
}
