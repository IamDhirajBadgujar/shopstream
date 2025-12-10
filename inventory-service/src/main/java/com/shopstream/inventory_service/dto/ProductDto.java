package com.shopstream.inventory_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

//inventory-service
public class ProductDto {
 String id;
 @NotBlank(message = "Name must not be blank")
 String name;
 
 @NotNull(message = "Price is required")
 @Positive(message = "Price must be positive")
 BigDecimal price;
 
 @NotNull(message = "Stock is required")
 @PositiveOrZero(message = "Stock must be zero or positive")
 Integer stock;
 
 private String supplierId;
 public String getSupplierId() {
	return supplierId;
}
 public void setSupplierId(String supplierId) {
	this.supplierId = supplierId;
 }
 public ProductDto(String id, String name, BigDecimal price, Integer stock) {
	this.id=id;
	this.name=name;
	this.price=price;
	this.stock=stock;
}
 public ProductDto() {
	// TODO Auto-generated constructor stub
}
 public String getId() {
	return id;
 }
 public void setId(String id) {
	this.id = id;
 }
 public String getName() {
	return name;
 }
 public void setName(String name) {
	this.name = name;
 }
 public BigDecimal getPrice() {
	return price;
 }
 public void setPrice(BigDecimal price) {
	this.price = price;
 }
 public Integer getStock() {
	return stock;
 }
 public void setStock(Integer stock) {
	this.stock = stock;
 }
 
}