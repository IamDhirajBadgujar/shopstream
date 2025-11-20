package com.shopstream.order_service.controller;


import com.shopstream.order_service.dto.CreateOrderRequest;
import com.shopstream.order_service.entity.Order;
import com.shopstream.order_service.service.OrderService;

import java.util.Map;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {
    private final OrderService svc;

    public OrderController(OrderService svc) { this.svc = svc; }
    
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> create(@RequestBody CreateOrderRequest req, Authentication auth) {
        // you can get username: auth.getName()
        Order saved = svc.createOrder(req);
        return ResponseEntity.status(201).body(Map.of("orderId", saved.getId(), "status", "CREATED"));
    }


    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateOrderRequest req) {
        Order saved = svc.createOrder(req);
        return ResponseEntity.status(201).body(new java.util.HashMap<String, Object>() {{
            put("orderId", saved.getId());
            put("status", "CREATED");
        }});
    }
}
