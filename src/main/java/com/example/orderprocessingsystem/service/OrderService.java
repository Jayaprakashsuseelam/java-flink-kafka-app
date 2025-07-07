package com.example.orderprocessingsystem.service;

import com.example.orderprocessingsystem.model.Order;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    
    // Create a new order
    Order createOrder(Order order);
    
    // Get an order by its ID
    Order getOrderById(Long id);
    
    // Get all orders
    List<Order> getAllOrders();
    
    // Update an existing order
    Order updateOrder(Long id, Order orderDetails);
    
    // Delete an order
    void deleteOrder(Long id);
    
    // Find orders by customer email
    List<Order> findOrdersByCustomerEmail(String email);
    
    // Find orders by status
    List<Order> findOrdersByStatus(Order.OrderStatus status);
    
    // Find orders created between two dates
    List<Order> findOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    // Update order status
    Order updateOrderStatus(Long id, Order.OrderStatus status);
}