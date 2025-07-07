package com.example.orderprocessingsystem.repository;

import com.example.orderprocessingsystem.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    // Find items by order id
    List<OrderItem> findByOrderId(Long orderId);
    
    // Find items by product code
    List<OrderItem> findByProductCode(String productCode);
    
    // Find items by product name containing the given string (case insensitive)
    List<OrderItem> findByProductNameContainingIgnoreCase(String productName);
}