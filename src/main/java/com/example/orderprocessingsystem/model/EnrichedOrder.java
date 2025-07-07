package com.example.orderprocessingsystem.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrichedOrder {
    private String orderId;
    private String userId;
    private String itemId;
    private int quantity;
    private LocalDateTime orderTime;
    
    // Enriched fields
    private String customerName;
    private String itemName;
    private Double itemPrice;
    private Double totalAmount;
    private String orderStatus;
    private LocalDateTime processedTime;
    
    public EnrichedOrder(OrderEvent orderEvent) {
        this.orderId = orderEvent.getOrderId();
        this.userId = orderEvent.getUserId();
        this.itemId = orderEvent.getItemId();
        this.quantity = orderEvent.getQuantity();
        this.orderTime = orderEvent.getOrderTime();
        this.processedTime = LocalDateTime.now();
    }
    
    public EnrichedOrder(OrderEvent orderEvent, String orderStatus, LocalDateTime processedTime) {
        this.orderId = orderEvent.getOrderId();
        this.userId = orderEvent.getUserId();
        this.itemId = orderEvent.getItemId();
        this.quantity = orderEvent.getQuantity();
        this.orderTime = orderEvent.getOrderTime();
        this.orderStatus = orderStatus;
        this.processedTime = processedTime;
    }
} 