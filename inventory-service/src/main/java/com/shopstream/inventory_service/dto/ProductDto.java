package com.shopstream.inventory_service.dto;

import java.math.BigDecimal;

//inventory-service
public class ProductDto {
 String id;
 String name;
 BigDecimal price;
 Integer stock;
 public ProductDto(String id, String name, BigDecimal price, Integer stock) {
	this.id=id;
	this.name=name;
	this.price=price;
	this.stock=stock;
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