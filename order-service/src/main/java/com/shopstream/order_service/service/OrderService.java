package com.shopstream.order_service.service;



import com.shopstream.order_service.dto.CreateOrderRequest;
import com.shopstream.order_service.dto.OrderCreatedEvent;
import com.shopstream.order_service.dto.OrderDetails;
import com.shopstream.order_service.entity.Order;
import com.shopstream.order_service.entity.OrderItem;
import com.shopstream.order_service.repository.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderService(OrderRepository orderRepo, KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderRepo = orderRepo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest req) {
        Order order = new Order();
        order.setShippingAddress(req.getShippingAddress());
        order.setUserId(req.getUserId());
        List<OrderItem> items = req.getItems().stream().map(l -> {
            OrderItem oi = new OrderItem();
            oi.setProductId(l.getProductId());
            oi.setQty(l.getQty());
            oi.setProductName(l.getProductName());
            oi.setPrice(BigDecimal.valueOf(l.getPrice() == null ? 0.0 : l.getPrice()));
            oi.setOrder(order);
            return oi;
        }).collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setItems(items);
        order.setTotal(total);
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println(order.toString());
        Order saved = orderRepo.save(order);

        // publish event
        OrderCreatedEvent evt = new OrderCreatedEvent(saved.getId(),
                items.stream().map(i -> new OrderCreatedEvent.OrderLine(i.getProductId(), i.getQty())).collect(Collectors.toList()));
        kafkaTemplate.send("orders.created", evt);

        return saved;
    }
    
    public OrderDetails toOrderDetails(Order order) {
        OrderDetails dto = new OrderDetails();
        dto.setOrderId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setTotal(order.getTotal());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setProductName(order.getProductName());
        List<OrderDetails.OrderItemDetails> itemsDto = order.getItems().stream()
                .map(oi -> {
                    OrderDetails.OrderItemDetails idto = new OrderDetails.OrderItemDetails();
                    idto.setId(oi.getId());
                    idto.setProductId(oi.getProductId());
                    idto.setQty(oi.getQty());
                    idto.setPrice(oi.getPrice());
                    return idto;
                })
                .toList();

        dto.setItems(itemsDto);
        return dto;
    }

    
    public Order createOrder(Long userId, CreateOrderRequest req) {
        // validate items, calculate total, etc.

        Order order = new Order();
        order.setUserId(userId);
        order.setShippingAddress(req.getShippingAddress());
        order.setCreatedAt(LocalDateTime.now());

        // compute total & items
        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CreateOrderRequest.Line dto : req.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProductId(dto.getProductId());
            oi.setQty(dto.getQty());
            oi.setProductName(dto.getProductName());

            BigDecimal price =new BigDecimal("110.23"); // your logic
            oi.setPrice(price);
            total = total.add(price.multiply(BigDecimal.valueOf(dto.getQty())));

            items.add(oi);
        }

        order.setItems(items);
        order.setTotal(total);

        return orderRepo.save(order);
    }

	
}
