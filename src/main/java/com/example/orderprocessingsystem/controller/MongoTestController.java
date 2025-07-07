package com.example.orderprocessingsystem.controller;

import com.example.orderprocessingsystem.model.EnrichedOrder;
import com.example.orderprocessingsystem.service.MongoOrderService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mongo-test")
public class MongoTestController {

    @Autowired
    private MongoOrderService mongoOrderService;

    @Autowired
    private MongoClient mongoClient;

    // Test endpoint to create a simple order and verify it's saved
    @PostMapping("/create-test-order")
    public ResponseEntity<Map<String, Object>> createTestOrder() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Create a test order
            EnrichedOrder testOrder = new EnrichedOrder();
            testOrder.setOrderId("TEST-" + System.currentTimeMillis());
            testOrder.setUserId("testuser");
            testOrder.setItemId("ITEM-TEST");
            testOrder.setQuantity(1);
            testOrder.setOrderTime(LocalDateTime.now());
            testOrder.setCustomerName("Test Customer");
            testOrder.setItemName("Test Item");
            testOrder.setItemPrice(99.99);
            testOrder.setTotalAmount(99.99);
            testOrder.setOrderStatus("PENDING");
            testOrder.setProcessedTime(LocalDateTime.now());

            // Save the order
            EnrichedOrder savedOrder = mongoOrderService.saveEnrichedOrder(testOrder);
            
            response.put("success", true);
            response.put("message", "Test order created successfully");
            response.put("orderId", savedOrder.getOrderId());
            response.put("database", "orderDB");
            response.put("collection", "enrichedOrder");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating test order: " + e.getMessage());
            response.put("error", e.toString());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Test endpoint to list all orders in the database
    @GetMapping("/list-all-orders")
    public ResponseEntity<Map<String, Object>> listAllOrders() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            MongoDatabase database = mongoClient.getDatabase("orderDB");
            MongoCollection<Document> collection = database.getCollection("enrichedOrder");
            
            List<Document> orders = new ArrayList<>();
            collection.find().into(orders);
            
            response.put("success", true);
            response.put("database", "orderDB");
            response.put("collection", "enrichedOrder");
            response.put("totalOrders", orders.size());
            response.put("orders", orders);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error listing orders: " + e.getMessage());
            response.put("error", e.toString());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Test endpoint to check database connection and collections
    @GetMapping("/database-info")
    public ResponseEntity<Map<String, Object>> getDatabaseInfo() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            MongoDatabase database = mongoClient.getDatabase("orderDB");
            
            List<String> collections = new ArrayList<>();
            database.listCollectionNames().into(collections);
            
            response.put("success", true);
            response.put("database", "orderDB");
            response.put("collections", collections);
            response.put("timestamp", LocalDateTime.now().toString());
            
            // Check if enrichedOrder collection exists
            if (collections.contains("enrichedOrder")) {
                MongoCollection<Document> collection = database.getCollection("enrichedOrder");
                long count = collection.countDocuments();
                response.put("enrichedOrderCount", count);
            } else {
                response.put("enrichedOrderCount", 0);
                response.put("warning", "enrichedOrder collection not found");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error getting database info: " + e.getMessage());
            response.put("error", e.toString());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Test endpoint to find orders by user ID
    @GetMapping("/orders-by-user/{userId}")
    public ResponseEntity<Map<String, Object>> getOrdersByUser(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<EnrichedOrder> orders = mongoOrderService.findEnrichedOrdersByUserId(userId);
            
            response.put("success", true);
            response.put("userId", userId);
            response.put("orderCount", orders.size());
            response.put("orders", orders);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error finding orders for user: " + e.getMessage());
            response.put("error", e.toString());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Test endpoint to get order statistics
    @GetMapping("/order-stats")
    public ResponseEntity<Map<String, Object>> getOrderStats() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            long totalOrders = mongoOrderService.countAllEnrichedOrders();
            long pendingOrders = mongoOrderService.countEnrichedOrdersByStatus("PENDING");
            long processedOrders = mongoOrderService.countEnrichedOrdersByStatus("PROCESSED");
            long cancelledOrders = mongoOrderService.countEnrichedOrdersByStatus("CANCELLED");
            
            response.put("success", true);
            response.put("totalOrders", totalOrders);
            response.put("pendingOrders", pendingOrders);
            response.put("processedOrders", processedOrders);
            response.put("cancelledOrders", cancelledOrders);
            response.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error getting order stats: " + e.getMessage());
            response.put("error", e.toString());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 