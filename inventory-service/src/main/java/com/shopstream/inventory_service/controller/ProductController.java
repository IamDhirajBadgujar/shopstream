package com.shopstream.inventory_service.controller;


import com.shopstream.inventory_service.dto.ProductDto;
import com.shopstream.inventory_service.model.Product;
import com.shopstream.inventory_service.repo.ProductRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200")  
public class ProductController {

	
    private final ProductRepository productRepo;

    public ProductController(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    @GetMapping
    public List<Product> getAll() {
        return productRepo.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable String id) {
        var p = productRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        var dto = new ProductDto(
            p.getId(),
            p.getName(),
            p.getPrice(),
            p.getStock()
        );
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "latest") String sort){
    	
    	
    	List<Product> products =productRepo.searchProducts(
                q,
                category,
                minPrice,
                maxPrice,
                inStock,
                sort
        );
    	return ResponseEntity.ok(products);
    	
    }
}
