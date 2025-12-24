package com.shopstream.inventory_service.repo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.shopstream.inventory_service.model.Product;


@Repository
public class ProductSearchRepositoryImpl implements ProductSearchRepository {

    private final MongoTemplate mongoTemplate;

    public ProductSearchRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Product> searchProducts(
            String q,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            String sort) {

        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();

        if (q != null && !q.isBlank()) {
            criteria.add(Criteria.where("name").regex(q, "i"));
        }

        if (category != null) {
            criteria.add(Criteria.where("category").is(category));
        }

        if (minPrice != null) {
            criteria.add(Criteria.where("price").gte(minPrice));
        }

        if (maxPrice != null) {
            criteria.add(Criteria.where("price").lte(maxPrice));
        }

        if (Boolean.TRUE.equals(inStock)) {
            criteria.add(Criteria.where("stock").gt(0));
        }

        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria));
        }

        // Sorting
        if ("priceAsc".equals(sort)) {
            query.with(Sort.by(Sort.Direction.ASC, "price"));
        } else if ("priceDesc".equals(sort)) {
            query.with(Sort.by(Sort.Direction.DESC, "price"));
        } else {
            query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        return mongoTemplate.find(query, Product.class);
    }

	
}
