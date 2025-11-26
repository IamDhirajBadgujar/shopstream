package com.shopstream.order_service.dto;

//order-service/src/main/java/com/shopstream/order_service/controller/MergeCartRequest.java

import com.shopstream.order_service.dto.CartItemDTO;
import java.util.List;

public class MergeCartRequest {
 private Long userId;
 private List<CartItemDTO> items;

 public Long getUserId() { return userId; }
 public void setUserId(Long userId) { this.userId = userId; }

 public List<CartItemDTO> getItems() { return items; }
 public void setItems(List<CartItemDTO> items) { this.items = items; }
}
