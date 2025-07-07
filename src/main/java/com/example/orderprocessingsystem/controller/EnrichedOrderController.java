package com.example.orderprocessingsystem.controller;

import com.example.orderprocessingsystem.model.EnrichedOrder;
import com.example.orderprocessingsystem.service.MongoOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/enriched-orders")
public class EnrichedOrderController {

    @Autowired
    private MongoOrderService mongoOrderService;

    // Get enriched orders by user ID
    @GetMapping("/{userId}")
    public ResponseEntity<List<EnrichedOrder>> getEnrichedOrdersByUserId(@PathVariable String userId) {
        List<EnrichedOrder> orders = mongoOrderService.findEnrichedOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    // Get enriched order by ID
    @GetMapping("/order/{orderId}")
    public ResponseEntity<EnrichedOrder> getEnrichedOrderById(@PathVariable String orderId) {
        Optional<EnrichedOrder> order = mongoOrderService.findEnrichedOrderById(orderId);
        return order.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    // Get enriched orders by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EnrichedOrder>> getEnrichedOrdersByStatus(@PathVariable String status) {
        List<EnrichedOrder> orders = mongoOrderService.findEnrichedOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    // Get enriched orders by date range
    @GetMapping("/date-range")
    public ResponseEntity<List<EnrichedOrder>> getEnrichedOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<EnrichedOrder> orders = mongoOrderService.findEnrichedOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    // Get enriched orders by user ID and status
    @GetMapping("/{userId}/status/{status}")
    public ResponseEntity<List<EnrichedOrder>> getEnrichedOrdersByUserIdAndStatus(
            @PathVariable String userId, @PathVariable String status) {
        List<EnrichedOrder> orders = mongoOrderService.findEnrichedOrdersByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(orders);
    }

    // Get enriched orders by minimum amount
    @GetMapping("/min-amount/{minAmount}")
    public ResponseEntity<List<EnrichedOrder>> getEnrichedOrdersByMinAmount(@PathVariable Double minAmount) {
        List<EnrichedOrder> orders = mongoOrderService.findEnrichedOrdersByMinAmount(minAmount);
        return ResponseEntity.ok(orders);
    }

    // Get enriched orders by item name
    @GetMapping("/item/{itemName}")
    public ResponseEntity<List<EnrichedOrder>> getEnrichedOrdersByItemName(@PathVariable String itemName) {
        List<EnrichedOrder> orders = mongoOrderService.findEnrichedOrdersByItemName(itemName);
        return ResponseEntity.ok(orders);
    }

    // Get enriched orders by customer name
    @GetMapping("/customer/{customerName}")
    public ResponseEntity<List<EnrichedOrder>> getEnrichedOrdersByCustomerName(@PathVariable String customerName) {
        List<EnrichedOrder> orders = mongoOrderService.findEnrichedOrdersByCustomerName(customerName);
        return ResponseEntity.ok(orders);
    }

    // Get top orders by amount
    @GetMapping("/top-orders")
    public ResponseEntity<List<EnrichedOrder>> getTopOrdersByAmount() {
        List<EnrichedOrder> orders = mongoOrderService.findTopOrdersByAmount();
        return ResponseEntity.ok(orders);
    }

    // Get recent orders
    @GetMapping("/recent")
    public ResponseEntity<List<EnrichedOrder>> getRecentOrders() {
        List<EnrichedOrder> orders = mongoOrderService.findRecentOrders();
        return ResponseEntity.ok(orders);
    }

    // Get enriched orders by price range
    @GetMapping("/price-range")
    public ResponseEntity<List<EnrichedOrder>> getEnrichedOrdersByPriceRange(
            @RequestParam Double minPrice, @RequestParam Double maxPrice) {
        List<EnrichedOrder> orders = mongoOrderService.findEnrichedOrdersByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(orders);
    }

    // Advanced search
    @GetMapping("/search")
    public ResponseEntity<List<EnrichedOrder>> searchEnrichedOrders(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<EnrichedOrder> orders = mongoOrderService.searchEnrichedOrders(userId, itemName, status, startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    // Create enriched order
    @PostMapping
    public ResponseEntity<EnrichedOrder> createEnrichedOrder(@RequestBody EnrichedOrder enrichedOrder) {
        EnrichedOrder savedOrder = mongoOrderService.saveEnrichedOrder(enrichedOrder);
        return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
    }

    // Create multiple enriched orders
    @PostMapping("/bulk")
    public ResponseEntity<List<EnrichedOrder>> createEnrichedOrders(@RequestBody List<EnrichedOrder> enrichedOrders) {
        List<EnrichedOrder> savedOrders = mongoOrderService.saveEnrichedOrders(enrichedOrders);
        return new ResponseEntity<>(savedOrders, HttpStatus.CREATED);
    }

    // Update enriched order
    @PutMapping("/{orderId}")
    public ResponseEntity<Boolean> updateEnrichedOrder(
            @PathVariable String orderId, @RequestBody EnrichedOrder enrichedOrder) {
        boolean updated = mongoOrderService.updateEnrichedOrder(orderId, enrichedOrder);
        return updated ? ResponseEntity.ok(true) : ResponseEntity.notFound().build();
    }

    // Update enriched order status
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Boolean> updateEnrichedOrderStatus(
            @PathVariable String orderId, @RequestParam String status) {
        boolean updated = mongoOrderService.updateEnrichedOrderStatus(orderId, status);
        return updated ? ResponseEntity.ok(true) : ResponseEntity.notFound().build();
    }

    // Delete enriched order
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Boolean> deleteEnrichedOrder(@PathVariable String orderId) {
        boolean deleted = mongoOrderService.deleteEnrichedOrder(orderId);
        return deleted ? ResponseEntity.ok(true) : ResponseEntity.notFound().build();
    }

    // Delete enriched orders by user ID
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Long> deleteEnrichedOrdersByUserId(@PathVariable String userId) {
        long deletedCount = mongoOrderService.deleteEnrichedOrdersByUserId(userId);
        return ResponseEntity.ok(deletedCount);
    }

    // Bulk update order statuses
    @PatchMapping("/bulk-status")
    public ResponseEntity<Void> bulkUpdateOrderStatuses(
            @RequestParam List<String> orderIds, @RequestParam String status) {
        mongoOrderService.bulkUpdateOrderStatuses(orderIds, status);
        return ResponseEntity.ok().build();
    }

    // Get statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", mongoOrderService.countAllEnrichedOrders());
        stats.put("pendingOrders", mongoOrderService.countEnrichedOrdersByStatus("PENDING"));
        stats.put("processedOrders", mongoOrderService.countEnrichedOrdersByStatus("PROCESSED"));
        stats.put("cancelledOrders", mongoOrderService.countEnrichedOrdersByStatus("CANCELLED"));
        return ResponseEntity.ok(stats);
    }

    // Get statistics by user ID
    @GetMapping("/stats/{userId}")
    public ResponseEntity<Map<String, Object>> getStatisticsByUserId(@PathVariable String userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", mongoOrderService.countEnrichedOrdersByUserId(userId));
        stats.put("pendingOrders", mongoOrderService.countEnrichedOrdersByStatus("PENDING"));
        stats.put("processedOrders", mongoOrderService.countEnrichedOrdersByStatus("PROCESSED"));
        stats.put("cancelledOrders", mongoOrderService.countEnrichedOrdersByStatus("CANCELLED"));
        return ResponseEntity.ok(stats);
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "MongoDB Enriched Orders Service is running!");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
} 