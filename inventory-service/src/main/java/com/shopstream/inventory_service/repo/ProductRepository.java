// src/main/java/com/shopstream/inventory_service/repo/ProductRepository.java
package com.shopstream.inventory_service.repo;

import com.shopstream.inventory_service.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    // 👇 supplierId is String now
    List<Product> findBySupplierId(String supplierId);
}
