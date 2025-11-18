package com.shopstream.inventory_service.repo;


import com.shopstream.inventory_service.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {}
