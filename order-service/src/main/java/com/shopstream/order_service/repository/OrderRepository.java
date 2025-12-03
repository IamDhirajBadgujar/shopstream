package com.shopstream.order_service.repository;


import com.shopstream.order_service.entity.Order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}
