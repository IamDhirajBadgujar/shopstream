package com.shopstream.order_service;
//order-service/src/main/java/com/shopstream/order_service/service/InventoryClient.java


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class InventoryClient {

    private final RestTemplate restTemplate;
    private final String inventoryBaseUrl;
    private static final Logger log =
            LoggerFactory.getLogger(InventoryClient.class);


    public InventoryClient(RestTemplate restTemplate,
                           @Value("${inventory.base-url}") String inventoryBaseUrl) {
        this.restTemplate = restTemplate;
        this.inventoryBaseUrl = inventoryBaseUrl;
    }

    // 🔹 Fetch product IDs for logged-in supplier
    public List<String> getMyProductIds() {

        String url = inventoryBaseUrl + "/api/products/my";
        log.debug("Calling Inventory Service URL: {}", url);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            log.error("Authentication is NULL in SecurityContext");
            return List.of();
        }

        Object credentials = auth.getCredentials();
        if (!(credentials instanceof String)) {
            log.error("JWT not found in credentials. Found: {}", credentials);
            return List.of();
        }

        String jwt = (String) credentials;
        log.debug("JWT extracted successfully (length={})", jwt.length());

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<ProductDto[]> response;
        try {
            response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ProductDto[].class
            );
        } catch (Exception e) {
            log.error("Error calling inventory-service /products/my", e);
            throw e;
        }

        log.debug("Inventory response status: {}", response.getStatusCode());

        ProductDto[] products = response.getBody();

        if (products == null) {
            log.warn("Inventory returned NULL product list");
            return List.of();
        }

        log.debug("Inventory returned {} products", products.length);

        return List.of(products)
                .stream()
                .map(ProductDto::getId)
                .peek(id -> log.debug("Product ID received: {}", id))
                .collect(Collectors.toList());
    }


    // 🔹 DTO matching inventory-service response
    public static class ProductDto {
        private String id;
        private String name;
        private Double price;
        private Integer stock;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }

        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
    }
}
