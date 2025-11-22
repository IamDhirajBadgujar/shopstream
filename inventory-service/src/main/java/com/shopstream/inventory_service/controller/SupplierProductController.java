package com.shopstream.inventory_service.controller;


import com.shopstream.inventory_service.model.Product;
import com.shopstream.inventory_service.repo.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supplier")
public class SupplierProductController {

    private final ProductRepository repo;

    public SupplierProductController(ProductRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@RequestBody Product product, Authentication auth) {
        // auth.getName() gives username from token (JwtAuthFilter sets Authentication)
        if (auth == null) {
            return ResponseEntity.status(401).build();
        }
        // Attach supplier username on the product
        product.setSupplier(auth.getName());
        Product saved = repo.save(product);
        return ResponseEntity.status(201).body(saved);
    }
}
