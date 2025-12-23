// src/main/java/com/shopstream/inventory_service/repo/ProductRepository.java
package com.shopstream.inventory_service.repo;

import com.shopstream.inventory_service.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    // 👇 supplierId is String now
    List<Product> findBySupplierId(String supplierId);

    @Query("""
    		SELECT p FROM Product p
    		WHERE (:q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')))
    		AND (:category IS NULL OR p.category = :category)
    		AND (:minPrice IS NULL OR p.price >= :minPrice)
    		AND (:maxPrice IS NULL OR p.price <= :maxPrice)
    		AND (:inStock IS NULL OR (p.stock > 0))
    		ORDER BY
    		    CASE WHEN :sort = 'priceAsc' THEN p.price END ASC,
    		    CASE WHEN :sort = 'priceDesc' THEN p.price END DESC,
    		    CASE WHEN :sort = 'latest' THEN p.createdAt END DESC
    		""")
    		List<Product> searchProducts(
    		    @Param("q") String q,
    		    @Param("category") String category,
    		    @Param("minPrice") BigDecimal minPrice,
    		    @Param("maxPrice") BigDecimal maxPrice,
    		    @Param("inStock") Boolean inStock,
    		    @Param("sort") String sort
    		);
}
