package com.example.orderprocessingsystem.controller;

import com.example.orderprocessingsystem.model.EnrichedOrder;
import com.example.orderprocessingsystem.service.MongoTransactionService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mongo-transactions")
public class MongoTransactionController {

    @Autowired
    private MongoTransactionService mongoTransactionService;

    // Transfer orders between users
    @PostMapping("/transfer-orders")
    public ResponseEntity<Map<String, Object>> transferOrdersBetweenUsers(
            @RequestParam String fromUserId,
            @RequestParam String toUserId,
            @RequestBody List<String> orderIds) {
        
        boolean success = mongoTransactionService.transferOrdersBetweenUsers(fromUserId, toUserId, orderIds);
        
        Map<String, Object> response = Map.of(
            "success", success,
            "message", success ? "Orders transferred successfully" : "Failed to transfer orders",
            "fromUserId", fromUserId,
            "toUserId", toUserId,
            "orderCount", orderIds.size()
        );
        
        return success ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    // Bulk status update with validation
    @PatchMapping("/bulk-status-update")
    public ResponseEntity<Map<String, Object>> bulkStatusUpdateWithValidation(
            @RequestParam List<String> orderIds,
            @RequestParam String newStatus,
            @RequestParam String userId) {
        
        boolean success = mongoTransactionService.bulkStatusUpdateWithValidation(orderIds, newStatus, userId);
        
        Map<String, Object> response = Map.of(
            "success", success,
            "message", success ? "Status updated successfully" : "Failed to update status",
            "userId", userId,
            "newStatus", newStatus,
            "orderCount", orderIds.size()
        );
        
        return success ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    // Create order with inventory check
    @PostMapping("/create-order-with-inventory")
    public ResponseEntity<Map<String, Object>> createOrderWithInventoryCheck(
            @RequestBody EnrichedOrder enrichedOrder,
            @RequestParam int availableQuantity) {
        
        boolean success = mongoTransactionService.createOrderWithInventoryCheck(enrichedOrder, availableQuantity);
        
        Map<String, Object> response = Map.of(
            "success", success,
            "message", success ? "Order created successfully" : "Failed to create order",
            "orderId", enrichedOrder.getOrderId(),
            "requestedQuantity", enrichedOrder.getQuantity(),
            "availableQuantity", availableQuantity
        );
        
        return success ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    // Delete user orders with cleanup
    @DeleteMapping("/delete-user-orders/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUserOrdersWithCleanup(@PathVariable String userId) {
        
        boolean success = mongoTransactionService.deleteUserOrdersWithCleanup(userId);
        
        Map<String, Object> response = Map.of(
            "success", success,
            "message", success ? "User orders deleted successfully" : "Failed to delete user orders",
            "userId", userId
        );
        
        return success ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    // Update order with audit trail
    @PutMapping("/update-order-with-audit/{orderId}")
    public ResponseEntity<Map<String, Object>> updateOrderWithAuditTrail(
            @PathVariable String orderId,
            @RequestBody EnrichedOrder updatedOrder) {
        
        boolean success = mongoTransactionService.updateOrderWithAuditTrail(orderId, updatedOrder);
        
        Map<String, Object> response = Map.of(
            "success", success,
            "message", success ? "Order updated with audit trail" : "Failed to update order",
            "orderId", orderId
        );
        
        return success ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    // Get transaction statistics
    @GetMapping("/statistics")
    public ResponseEntity<Document> getTransactionStatistics() {
        Document stats = mongoTransactionService.getTransactionStatistics();
        return ResponseEntity.ok(stats);
    }

    // Health check for transaction service
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = Map.of(
            "status", "UP",
            "message", "MongoDB Transaction Service is running!",
            "timestamp", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(response);
    }
} 