package com.shopstream.order_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

//order-service
@Configuration
public class RestClientConfig {

 @Bean
 public RestTemplate restTemplate() {
     return new RestTemplate();
 }
}
