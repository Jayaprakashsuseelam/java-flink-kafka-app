package com.example.orderprocessingsystem.repository.mongo;

import com.example.orderprocessingsystem.model.EnrichedOrder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EnrichedOrderMongoRepository extends MongoRepository<EnrichedOrder, String> {

    // Find orders by user ID
    List<EnrichedOrder> findByUserId(String userId);

    // Find orders by status
    List<EnrichedOrder> findByOrderStatus(String orderStatus);

    // Find orders by date range
    List<EnrichedOrder> findByOrderTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find orders by user ID and status
    List<EnrichedOrder> findByUserIdAndOrderStatus(String userId, String orderStatus);

    // Find orders with total amount greater than specified value
    List<EnrichedOrder> findByTotalAmountGreaterThan(Double amount);

    // Find orders by item name (case-insensitive)
    @Query("{'itemName': {$regex: ?0, $options: 'i'}}")
    List<EnrichedOrder> findByItemNameContainingIgnoreCase(String itemName);

    // Find orders by customer name (case-insensitive)
    @Query("{'customerName': {$regex: ?0, $options: 'i'}}")
    List<EnrichedOrder> findByCustomerNameContainingIgnoreCase(String customerName);

    // Find orders by user ID and date range
    List<EnrichedOrder> findByUserIdAndOrderTimeBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);

    // Count orders by status
    long countByOrderStatus(String orderStatus);

    // Count orders by user ID
    long countByUserId(String userId);

    // Find top orders by total amount
    List<EnrichedOrder> findTop10ByOrderByTotalAmountDesc();

    // Find recent orders
    List<EnrichedOrder> findTop20ByOrderByOrderTimeDesc();

    // Find orders with specific item price range
    @Query("{'itemPrice': {$gte: ?0, $lte: ?1}}")
    List<EnrichedOrder> findByItemPriceBetween(Double minPrice, Double maxPrice);

    // Find orders by multiple statuses
    @Query("{'orderStatus': {$in: ?0}}")
    List<EnrichedOrder> findByOrderStatusIn(List<String> statuses);

    // Find orders by user ID and item name
    @Query("{'userId': ?0, 'itemName': {$regex: ?1, $options: 'i'}}")
    List<EnrichedOrder> findByUserIdAndItemNameContainingIgnoreCase(String userId, String itemName);
} 