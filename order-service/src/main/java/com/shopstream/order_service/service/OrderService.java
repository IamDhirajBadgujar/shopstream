package com.shopstream.order_service.service;



import com.shopstream.order_service.dto.CreateOrderRequest;
import com.shopstream.order_service.dto.OrderCreatedEvent;
import com.shopstream.order_service.entity.Order;
import com.shopstream.order_service.entity.OrderItem;
import com.shopstream.order_service.repository.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

        List<OrderItem> items = req.getItems().stream().map(l -> {
            OrderItem oi = new OrderItem();
            oi.setProductId(l.getProductId());
            oi.setQty(l.getQty());
            oi.setPrice(BigDecimal.valueOf(l.getPrice() == null ? 0.0 : l.getPrice()));
            oi.setOrder(order);
            return oi;
        }).collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setItems(items);
        order.setTotal(total);

        Order saved = orderRepo.save(order);

        // publish event
        OrderCreatedEvent evt = new OrderCreatedEvent(saved.getId(),
                items.stream().map(i -> new OrderCreatedEvent.OrderLine(i.getProductId(), i.getQty())).collect(Collectors.toList()));
        kafkaTemplate.send("orders.created", evt);

        return saved;
    }
}
