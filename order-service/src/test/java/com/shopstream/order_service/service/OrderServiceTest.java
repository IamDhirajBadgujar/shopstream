package com.shopstream.order_service.service;

import com.shopstream.order_service.dto.CreateOrderRequest;
import com.shopstream.order_service.entity.Order;
import com.shopstream.order_service.entity.OrderItem;
import com.shopstream.order_service.repository.OrderRepository;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private com.shopstream.order_service.InventoryClient inventoryClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testCreateOrder_success() {
        // Arrange
        List<OrderItem> items = List.of(new OrderItem());
        CreateOrderRequest req = new CreateOrderRequest();

       // when(inventoryClient.checkStock(items)).thenReturn(true);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(100L);
            return o;
        });

        // Act
        Order savedOrder = orderService.createOrder(req);

        // Assert
        assertEquals(100L, savedOrder.getId());
        assertEquals(1L, savedOrder.getUserId());
      //  assertEquals("CREATED", savedOrder.getStatus());

       // verify(inventoryClient, times(1)).checkStock(items);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCreateOrder_nullRequest_throwsException() {
        // Assert
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(null);
        });
    }
}
