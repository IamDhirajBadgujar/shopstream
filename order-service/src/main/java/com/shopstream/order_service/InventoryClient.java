package com.shopstream.order_service;
//order-service/src/main/java/com/shopstream/order_service/service/InventoryClient.java


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InventoryClient {

 private final RestTemplate restTemplate;
 private final String inventoryBaseUrl;

 public InventoryClient(RestTemplate restTemplate,
                        @Value("${inventory.base-url}") String inventoryBaseUrl) {
     this.restTemplate = restTemplate;
     this.inventoryBaseUrl = inventoryBaseUrl;
 }

 public ProductDto getProduct(String string) {
     String url = inventoryBaseUrl + "/api/products/{id}";
     return restTemplate.getForObject(url, ProductDto.class, string);
 }

 // Match this with inventory-service's response JSON
 public static class ProductDto {
     private String id;
     private String name;
     private Double price;
     private Integer stock;

     public String getId() { return id; }
     public void setId(String id) { this.id = id; }

     public String getName() { return name; }
     public void setName(String name) { this.name = name; }

     public Double getPrice() { return price; }
     public void setPrice(Double price) { this.price = price; }

     public Integer getStock() { return stock; }
     public void setStock(Integer stock) { this.stock = stock; }
 }
}
