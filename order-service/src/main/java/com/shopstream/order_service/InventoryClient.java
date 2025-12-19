package com.shopstream.order_service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class InventoryClient {

    private static final Logger log =
            LoggerFactory.getLogger(InventoryClient.class);

    private final RestTemplate restTemplate;
    private final String inventoryBaseUrl;

	private HttpServletRequest request;

    public InventoryClient(
            RestTemplate restTemplate,
            @Value("${inventory.base-url}") String inventoryBaseUrl,
            HttpServletRequest request) {
    	this.request = request;
        this.restTemplate = restTemplate;
        this.inventoryBaseUrl = inventoryBaseUrl;
    }

    // 🔹 Fetch product IDs for logged-in supplier
    public List<String> getMyProductIds() {

        String url = inventoryBaseUrl + "/api/supplier/products/my";
        log.debug("Calling Inventory Service URL: {}", url);
        
        System.out.println("Inside the Inventoryclient for getMyProductIds------------");
        String jwt = extractJwt();
        System.out.println("getMyProductIds.JWT :-" + jwt);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<ProductDto[]> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        ProductDto[].class
                );

        ProductDto[] products = response.getBody();

        if (products == null) {
        	 System.out.println("Inventory returned null product list");
            log.warn("Inventory returned null product list");
            return List.of();
        }

        return List.of(products)
                .stream()
                .map(ProductDto::getId)
                .collect(Collectors.toList());
    }

    // 🔹 Fetch single product (JWT INCLUDED)
    public ProductDto getProduct(String productId) {
    	
        String url = inventoryBaseUrl + "/api/products/{id}";
        String jwt = extractJwt();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<ProductDto> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        ProductDto.class,
                        productId
                );

        return response.getBody();
    }

    // 🔹 Centralized JWT extraction (IMPORTANT)
    private String extractJwt() {

        System.out.println("Extracting JWT from request header");

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        System.out.println("Authorization header = " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalStateException("Authorization header missing");
        }

        return authHeader.substring(7);
    }


    // 🔹 DTO (can be moved later)
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
