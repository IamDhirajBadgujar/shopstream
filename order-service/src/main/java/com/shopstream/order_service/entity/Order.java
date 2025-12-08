package com.shopstream.order_service.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
  
	private String shippingAddress;
    private BigDecimal total;
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
    private List<OrderItem> items;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
	@Override
	public String toString() {
		return "Order [id=" + id + ", shippingAddress=" + shippingAddress + ", total=" + total + ", createdAt="
				+ createdAt + ", items=" + items.toString() + ", userId=" + userId + "]";
	}
	
	
	
}
