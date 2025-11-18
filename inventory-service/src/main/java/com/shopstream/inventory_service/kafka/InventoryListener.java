package com.shopstream.inventory_service.kafka;

import com.shopstream.inventory_service.dto.OrderCreatedEvent;
import com.shopstream.inventory_service.model.Product;
import com.shopstream.inventory_service.repo.ProductRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryListener {

    private final ProductRepository productRepo;

    public InventoryListener(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    @KafkaListener(topics = "orders.created", groupId = "${spring.kafka.consumer.group-id}")
    public void handleOrderCreated(OrderCreatedEvent event) {
        if (event == null || event.getItems() == null) return;
        for (OrderCreatedEvent.OrderLine line : event.getItems()) {
            productRepo.findById(line.getProductId()).ifPresent(p -> {
                int newStock = (p.getStock() == null ? 0 : p.getStock()) - (line.getQty() == null ? 0 : line.getQty());
                p.setStock(Math.max(newStock, 0));
                productRepo.save(p);
                System.out.println("InventoryService: reduced stock for product " + p.getId() + " -> " + p.getStock());
            });
        }
    }
}
