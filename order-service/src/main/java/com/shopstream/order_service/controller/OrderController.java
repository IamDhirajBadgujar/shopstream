package com.shopstream.order_service.controller;


import com.shopstream.order_service.DTO.CreateOrderRequest;
import com.shopstream.order_service.entity.Order;
import com.shopstream.order_service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService svc;

    public OrderController(OrderService svc) { this.svc = svc; }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateOrderRequest req) {
        Order saved = svc.createOrder(req);
        return ResponseEntity.status(201).body(new java.util.HashMap<String, Object>() {{
            put("orderId", saved.getId());
            put("status", "CREATED");
        }});
    }
}
