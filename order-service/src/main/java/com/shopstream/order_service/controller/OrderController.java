package com.shopstream.order_service.controller;


import com.shopstream.order_service.InventoryClient;
import com.shopstream.order_service.dto.CreateOrderRequest;
import com.shopstream.order_service.dto.OrderDetails;
import com.shopstream.order_service.dto.SupplierOrderItemDto;
import com.shopstream.order_service.entity.Order;
import com.shopstream.order_service.repository.OrderRepository;
import com.shopstream.order_service.security.CustomUserPrincipal;
import com.shopstream.order_service.service.OrderService;

import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {
    private final OrderService svc;
    
    @Autowired
	private OrderRepository orderRepository;

    public OrderController(OrderService svc ) { this.svc = svc; }
    @Autowired
    public InventoryClient InventoryClient;
    
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> create(@RequestBody CreateOrderRequest req) {
        
    	System.out.println("---------------------------creating Order /8080/api/order");
    	System.out.println(req.toString());
        Order saved = svc.createOrder(req);
        return ResponseEntity.status(201).body(Map.of("orderId", saved.getId(), "status", "CREATED"));
    }
    @GetMapping("/my")
    public List<OrderDetails> getMyOrders(@AuthenticationPrincipal CustomUserPrincipal principal) {
        Long userId = principal.getUserId();
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    	System.out.println(orders.toString());
        return orders.stream()
                .map(svc::toOrderDetails)
                .toList();
    }
    
    
        @GetMapping("/supplier-orders")
        public ResponseEntity<?> getSupplierOrders(
                @AuthenticationPrincipal CustomUserPrincipal principal) {

            if (principal == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }

            // You can safely use:
            Long supplierUserId = principal.getUserId();
            String username = principal.getUsername();

            // Inventory service resolves supplier via JWT
            List<String> productIds = null;
			try {
				productIds = InventoryClient.getMyProductIds();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            if (productIds.isEmpty()) {
                return ResponseEntity.ok(List.of());
            }

            List<SupplierOrderItemDto> orders =
                    orderRepository.findOrdersForSupplier(productIds);

            return ResponseEntity.ok(orders);
        }

    



//    @PostMapping
//    public ResponseEntity<?> create(@RequestBody CreateOrderRequest req) {
//        Order saved = svc.createOrder(req);
//        return ResponseEntity.status(201).body(new java.util.HashMap<String, Object>() {{
//            put("orderId", saved.getId());
//            put("status", "CREATED");
//        }});
//    }
}
