package com.example.orderprocessingsystem.config;

import com.example.orderprocessingsystem.model.EnrichedOrder;
import com.example.orderprocessingsystem.service.MongoOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
public class MongoDataInitializer {

    @Bean
    @Order(2) // Run after JPA data initialization
    public CommandLineRunner initMongoData(MongoOrderService mongoOrderService) {
        return args -> {
            // Check if data already exists
            if (mongoOrderService.countAllEnrichedOrders() > 0) {
                System.out.println("MongoDB data already initialized, skipping...");
                return;
            }

            // Create sample enriched orders
            List<EnrichedOrder> enrichedOrders = Arrays.asList(
                createEnrichedOrder("user001", "ORD-001", "John Doe", "Smartphone", 999.99, 1, 999.99, "PENDING", LocalDateTime.now().minusDays(2)),
                createEnrichedOrder("user001", "ORD-002", "John Doe", "Phone Case", 29.99, 2, 59.98, "PROCESSED", LocalDateTime.now().minusDays(1)),
                createEnrichedOrder("user002", "ORD-003", "Jane Smith", "Laptop", 1299.99, 1, 1299.99, "PROCESSED", LocalDateTime.now().minusDays(3)),
                createEnrichedOrder("user002", "ORD-004", "Jane Smith", "Mouse", 49.99, 1, 49.99, "CANCELLED", LocalDateTime.now().minusDays(4)),
                createEnrichedOrder("user003", "ORD-005", "Bob Johnson", "Headphones", 199.99, 1, 199.99, "PENDING", LocalDateTime.now().minusDays(1)),
                createEnrichedOrder("user003", "ORD-006", "Bob Johnson", "Keyboard", 89.99, 1, 89.99, "PROCESSED", LocalDateTime.now().minusDays(5)),
                createEnrichedOrder("user004", "ORD-007", "Alice Brown", "Tablet", 599.99, 1, 599.99, "PROCESSED", LocalDateTime.now().minusDays(6)),
                createEnrichedOrder("user004", "ORD-008", "Alice Brown", "Screen Protector", 19.99, 3, 59.97, "PENDING", LocalDateTime.now().minusDays(1)),
                createEnrichedOrder("user005", "ORD-009", "Charlie Wilson", "Gaming Console", 499.99, 1, 499.99, "PROCESSED", LocalDateTime.now().minusDays(7)),
                createEnrichedOrder("user005", "ORD-010", "Charlie Wilson", "Game Controller", 79.99, 2, 159.98, "CANCELLED", LocalDateTime.now().minusDays(2))
            );

            // Save enriched orders
            mongoOrderService.saveEnrichedOrders(enrichedOrders);

            System.out.println("MongoDB sample data initialized with " + enrichedOrders.size() + " enriched orders!");
        };
    }

    private EnrichedOrder createEnrichedOrder(String userId, String orderId, String customerName, 
                                             String itemName, Double itemPrice, Integer quantity, 
                                             Double totalAmount, String orderStatus, LocalDateTime orderTime) {
        EnrichedOrder enrichedOrder = new EnrichedOrder();
        enrichedOrder.setUserId(userId);
        enrichedOrder.setOrderId(orderId);
        enrichedOrder.setItemId("ITEM-" + orderId);
        enrichedOrder.setQuantity(quantity);
        enrichedOrder.setOrderTime(orderTime);
        enrichedOrder.setCustomerName(customerName);
        enrichedOrder.setItemName(itemName);
        enrichedOrder.setItemPrice(itemPrice);
        enrichedOrder.setTotalAmount(totalAmount);
        enrichedOrder.setOrderStatus(orderStatus);
        enrichedOrder.setProcessedTime(LocalDateTime.now());
        return enrichedOrder;
    }
} 