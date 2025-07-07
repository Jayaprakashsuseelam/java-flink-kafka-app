package com.example.orderprocessingsystem.repository;

import com.example.orderprocessingsystem.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Find orders by customer email
    List<Order> findByCustomerEmail(String email);
    
    // Find orders by status
    List<Order> findByStatus(Order.OrderStatus status);
    
    // Find orders created between two dates
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find orders by customer name containing the given string (case insensitive)
    List<Order> findByCustomerNameContainingIgnoreCase(String customerName);
}