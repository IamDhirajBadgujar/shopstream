package com.shopstream.inventory_service.controller;

import com.shopstream.inventory_service.dto.ProductDto;
import com.shopstream.inventory_service.model.Product;
import com.shopstream.inventory_service.service.ProductService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/supplier")
public class SupplierProductController {

    @Autowired
    private ProductService productService;

    // DTO for create/update (controller-level request)
    public static class ProductRequest {
        @NotBlank(message = "name is required")
        public String name;

        @NotNull(message = "price is required")
        @Positive(message = "Price must be positive")
        public BigDecimal price;

        @NotNull(message = "stock is required")
        @PositiveOrZero(message = "stock must be zero or positive")
        public Integer stock;
    }

    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@Valid @RequestBody ProductRequest req, Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // This will be "Dhiraj-Supplier"
        String supplierId = auth.getName();
        System.out.println("Supplier (auth.getName) = " + supplierId);

        ProductDto p = new ProductDto();
        p.setName(req.name);
        p.setPrice(req.price);
        p.setStock(req.stock);
        p.setSupplierId(supplierId); // 👈 key line

        Product saved = productService.saveProduct(p);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/products/my")
    public ResponseEntity<?> getMyProducts(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String supplierId = auth.getName();
        System.out.println("Get my products for supplier = " + supplierId);

        List<Product> products = productService.findBySupplierId(supplierId);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequest req,
            Authentication auth
    ) {
        if (auth == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String supplierId = auth.getName();

        // Build a DTO for update
        ProductDto updateDto = new ProductDto();
        updateDto.setName(req.name);
        updateDto.setPrice(req.price);
        updateDto.setStock(req.stock);

        try {
            Product saved = productService.updateProduct(id, updateDto, supplierId);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            if (msg.contains("not found")) {
                return ResponseEntity.status(404).body("Product not found");
            } else if (msg.contains("cannot") || msg.contains("edit")) {
                return ResponseEntity.status(403).body("You cannot edit this product");
            } else {
                return ResponseEntity.status(400).body(e.getMessage());
            }
        }
    }
}
