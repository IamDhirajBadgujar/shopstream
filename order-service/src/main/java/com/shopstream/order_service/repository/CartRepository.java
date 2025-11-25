package com.shopstream.order_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopstream.order_service.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
	  Optional<Cart> findByUserId(Long userId);
	}
