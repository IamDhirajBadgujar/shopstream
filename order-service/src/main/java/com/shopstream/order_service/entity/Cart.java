package com.shopstream.order_service.entity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name="carts")
public class Cart {
  @Id @GeneratedValue private Long id;
  @Column(name="user_id", unique=true, nullable=false) private Long userId;
  @OneToMany(mappedBy="cart", cascade=CascadeType.ALL, orphanRemoval=true)
  private List<CartItem> items = new ArrayList<>();
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;
  public Cart() {
	super();
	// TODO Auto-generated constructor stub
}
  // getters/setters
  public Long getId() {
	return id;
  }
  public void setId(Long id) {
	this.id = id;
  }
  public Long getUserId() {
	return userId;
  }
  public void setUserId(Long userId) {
	this.userId = userId;
  }
  public List<CartItem> getItems() {
	return items;
  }
  public void setItems(List<CartItem> items) {
	this.items = items;
  }
  public Date getCreatedAt() { return createdAt; }
  public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}