package com.shopstream.inventory_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopstream.inventory_service.dto.ProductDto;
import com.shopstream.inventory_service.model.Product;
import com.shopstream.inventory_service.repo.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    /**
     * Create / save a product from DTO.
     * If productDto.id is null -> creates new, otherwise attempts to set given id.
     */
    public Product saveProduct(ProductDto productDto) {
        Product p = new Product();
        p.setId(productDto.getId());
        p.setName(productDto.getName());
        p.setPrice(productDto.getPrice());
        p.setStock(productDto.getStock());
        p.setSupplierId(productDto.getSupplierId());

        return repo.save(p);
    }

    /**
     * Find product by id.
     */
    public Optional<Product> findById(String id) {
        return repo.findById(id);
    }

    /**
     * Find all products for a given supplier id.
     */
    public List<Product> findBySupplierId(String supplierId) {
        return repo.findBySupplierId(supplierId);
    }

    /**
     * Update product fields if product exists and belongs to supplierId.
     * Returns updated product.
     * Throws RuntimeException if product not found or not owned by supplier.
     */
    @Transactional
    public Product updateProduct(String id, ProductDto updateDto, String supplierId) {
        Product existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (existing.getSupplierId() == null || !existing.getSupplierId().equals(supplierId)) {
            throw new RuntimeException("You cannot edit this product");
        }

        // update fields
        if (updateDto.getName() != null) existing.setName(updateDto.getName());
        if (updateDto.getPrice() != null) existing.setPrice(updateDto.getPrice());
        if (updateDto.getStock() != null) existing.setStock(updateDto.getStock());

        return repo.save(existing);
    }

    /**
     * Delete a product if it exists and belongs to supplierId.
     * Throws RuntimeException if not found / not owner.
     */
    @Transactional
    public void deleteProduct(String id, String supplierId) {
        Product existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (existing.getSupplierId() == null || !existing.getSupplierId().equals(supplierId)) {
            throw new RuntimeException("You cannot delete this product");
        }

        repo.deleteById(id);
    }
}
