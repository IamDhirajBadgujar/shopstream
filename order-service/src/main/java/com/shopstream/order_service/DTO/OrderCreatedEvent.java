package com.shopstream.order_service.dto;


import java.util.Date;
import java.util.List;

import com.shopstream.order_service.entity.User;

public class OrderCreatedEvent {
    private Long orderId;
    private List<OrderLine> items;
    
    private Date datecreated;
    public OrderCreatedEvent(Long orderId, List<OrderLine> items, Date datecreated, Long userid) {
		super();
		this.orderId = orderId;
		this.items = items;
		this.datecreated = datecreated;
		this.userid = userid;
	}
	public Date getDatecreated() {
		return datecreated;
	}
	public void setDatecreated(Date datecreated) {
		this.datecreated = datecreated;
	}
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	private Long userid;

    public static class OrderLine {
        private String productId;
        private Integer qty;

        public OrderLine() {}
        public OrderLine(String productId, Integer qty) { this.productId = productId; this.qty = qty; }

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public Integer getQty() { return qty; }
        public void setQty(Integer qty) { this.qty = qty; }
    }

    public OrderCreatedEvent() {}
    public OrderCreatedEvent(Long orderId, List<OrderLine> items) { this.orderId = orderId; this.items = items; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public List<OrderLine> getItems() { return items; }
    public void setItems(List<OrderLine> items) { this.items = items; }
}
