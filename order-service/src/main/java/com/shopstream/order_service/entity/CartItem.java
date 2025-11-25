	package com.shopstream.order_service.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
	@Table(name="cart_items")
	public class CartItem {
	  @Id @GeneratedValue private Long id;
	  @ManyToOne @JoinColumn(name="cart_id") private Cart cart;
	  private Long productId;
	  private Integer quantity;
	  @Temporal(TemporalType.TIMESTAMP)
	  private Date addedAt;
	  public Long getId() {
		  return id;
	  }
	  public void setId(Long id) {
		  this.id = id;
	  }
	  public Cart getCart() {
		  return cart;
	  }
	  public void setCart(Cart cart) {
		  this.cart = cart;
	  }
	  public Long getProductId() {
		  return productId;
	  }
	  public void setProductId(Long productId) {
		  this.productId = productId;
	  }
	  public Integer getQuantity() {
		  return quantity;
	  }
	  public void setQuantity(Integer quantity) {
		  this.quantity = quantity;
	  }
	  public CartItem() {
		super();
		// TODO Auto-generated constructor stub
	  }
	  public Date getAddedAt() { return addedAt; }
	  public void setAddedAt(Date addedAt) { this.addedAt = addedAt; }
	  
	  // getters/setters
	}