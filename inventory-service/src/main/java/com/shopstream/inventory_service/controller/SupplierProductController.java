// src/main/java/com/shopstream/inventory_service/controller/SupplierProductController.java
package com.shopstream.inventory_service.controller;

import com.shopstream.inventory_service.model.Product;
import com.shopstream.inventory_service.repo.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/supplier")
public class SupplierProductController {

    private final ProductRepository repo;

    public SupplierProductController(ProductRepository repo) {
        this.repo = repo;
    }

    // DTO for create/update
    public static class ProductRequest {
        public String name;
        public BigDecimal price;
        public Integer stock;
    }

    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@RequestBody ProductRequest req, Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // This will be "Dhiraj-Supplier"
        String supplierId = auth.getName();
        System.out.println("Supplier (auth.getName) = " + supplierId);

        Product p = new Product();
        p.setName(req.name);
        p.setPrice(req.price);
        p.setStock(req.stock);
        p.setSupplierId(supplierId); // 👈 key line

        Product saved = repo.save(p);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/products/my")
    public ResponseEntity<?> getMyProducts(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String supplierId = auth.getName();
        System.out.println("Get my products for supplier = " + supplierId);

        List<Product> products = repo.findBySupplierId(supplierId);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable String id,
            @RequestBody ProductRequest req,
            Authentication auth
    ) {
        if (auth == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String supplierId = auth.getName();

        Product p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // make sure only owner can update
        if (p.getSupplierId() == null || !p.getSupplierId().equals(supplierId)) {
            return ResponseEntity.status(403).body("You cannot edit this product");
        }

        p.setName(req.name);
        p.setPrice(req.price);
        p.setStock(req.stock);

        Product saved = repo.save(p);
        return ResponseEntity.ok(saved);
    }
}
