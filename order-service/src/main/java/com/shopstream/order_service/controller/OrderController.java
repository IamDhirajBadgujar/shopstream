package com.shopstream.order_service.controller;

import com.shopstream.order_service.InventoryClient;
import com.shopstream.order_service.dto.CreateOrderRequest;
import com.shopstream.order_service.dto.OrderDetails;
import com.shopstream.order_service.dto.SupplierOrderItemDto;
import com.shopstream.order_service.entity.Order;
import com.shopstream.order_service.repository.OrderRepository;
import com.shopstream.order_service.security.CustomUserPrincipal;
import com.shopstream.order_service.service.OrderService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public OrderController(
            OrderService orderService,
            OrderRepository orderRepository,
            InventoryClient inventoryClient) {

        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> create(@RequestBody CreateOrderRequest req) {

        Order saved = orderService.createOrder(req);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "orderId", saved.getId(),
                        "status", "CREATED"
                ));
    }

    @GetMapping("/my")
    public List<OrderDetails> getMyOrders(
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        Long userId = principal.getUserId();

        List<Order> orders =
                orderRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return orders.stream()
                .map(orderService::toOrderDetails)
                .toList();
    }

    @GetMapping("/supplier-orders")
    public ResponseEntity<?> getSupplierOrders(
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        if (principal == null) {
        	System.out.println("principle is null...exiting ...!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized");
        }
        System.out.println("principle is NOT null...Proceeding ahead ...!");
        List<String> productIds;
        try {
            productIds = inventoryClient.getMyProductIds();
        } catch (Exception e) {
        	System.out.println("productIds got some exception....Exiting");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Inventory service unavailable");
        }

        if (productIds.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<SupplierOrderItemDto> orders =
                orderRepository.findOrdersForSupplier(productIds);

        return ResponseEntity.ok(orders);
    }
}
