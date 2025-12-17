package com.shopstream.order_service.repository;


import com.shopstream.order_service.dto.SupplierOrderItemDto;
import com.shopstream.order_service.entity.Order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
	@Query("""
			SELECT new com.shopstream.order_service.dto.SupplierOrderItemDto(
			    o.id,
			    u.username,
			    o.shippingAddress,
			    oi.productId,
			    oi.productName,
			    oi.qty,
			    oi.price
			)
			FROM Order o
			JOIN o.items oi
			JOIN User u ON u.id = o.userId
			WHERE oi.productId IN :productIds
			ORDER BY o.id DESC
			""")
			List<SupplierOrderItemDto> findOrdersForSupplier(
			        @Param("productIds") List<String> productIds);
}
