package com.shopstream.inventory_service.repo;

import java.math.BigDecimal;
import java.util.List;

import com.shopstream.inventory_service.model.Product;

public interface  ProductSearchRepository {
	List<Product> searchProducts(
	        String q,
	        String category,
	        BigDecimal minPrice,
	        BigDecimal maxPrice,
	        Boolean inStock,
	        String sort
	    );
}
