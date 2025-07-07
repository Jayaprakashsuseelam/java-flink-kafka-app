package com.example.orderprocessingsystem.service;

import com.example.orderprocessingsystem.model.EnrichedOrder;
import com.example.orderprocessingsystem.repository.mongo.EnrichedOrderMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MongoOrderService {

    @Autowired
    private EnrichedOrderMongoRepository enrichedOrderRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    // Save enriched order with transaction support
    @Transactional
    public EnrichedOrder saveEnrichedOrder(EnrichedOrder enrichedOrder) {
        return enrichedOrderRepository.save(enrichedOrder);
    }

    // Save multiple enriched orders in a transaction
    @Transactional
    public List<EnrichedOrder> saveEnrichedOrders(List<EnrichedOrder> enrichedOrders) {
        return enrichedOrderRepository.saveAll(enrichedOrders);
    }

    // Find enriched order by ID
    public Optional<EnrichedOrder> findEnrichedOrderById(String id) {
        return enrichedOrderRepository.findById(id);
    }

    // Find enriched orders by user ID
    public List<EnrichedOrder> findEnrichedOrdersByUserId(String userId) {
        return enrichedOrderRepository.findByUserId(userId);
    }

    // Find enriched orders by status
    public List<EnrichedOrder> findEnrichedOrdersByStatus(String status) {
        return enrichedOrderRepository.findByOrderStatus(status);
    }

    // Find enriched orders by date range
    public List<EnrichedOrder> findEnrichedOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return enrichedOrderRepository.findByOrderTimeBetween(startDate, endDate);
    }

    // Find enriched orders by user ID and status
    public List<EnrichedOrder> findEnrichedOrdersByUserIdAndStatus(String userId, String status) {
        return enrichedOrderRepository.findByUserIdAndOrderStatus(userId, status);
    }

    // Find enriched orders with total amount greater than specified value
    public List<EnrichedOrder> findEnrichedOrdersByMinAmount(Double minAmount) {
        return enrichedOrderRepository.findByTotalAmountGreaterThan(minAmount);
    }

    // Find enriched orders by item name (case-insensitive)
    public List<EnrichedOrder> findEnrichedOrdersByItemName(String itemName) {
        return enrichedOrderRepository.findByItemNameContainingIgnoreCase(itemName);
    }

    // Find enriched orders by customer name (case-insensitive)
    public List<EnrichedOrder> findEnrichedOrdersByCustomerName(String customerName) {
        return enrichedOrderRepository.findByCustomerNameContainingIgnoreCase(customerName);
    }

    // Find top orders by total amount
    public List<EnrichedOrder> findTopOrdersByAmount() {
        return enrichedOrderRepository.findTop10ByOrderByTotalAmountDesc();
    }

    // Find recent orders
    public List<EnrichedOrder> findRecentOrders() {
        return enrichedOrderRepository.findTop20ByOrderByOrderTimeDesc();
    }

    // Find orders by item price range
    public List<EnrichedOrder> findEnrichedOrdersByPriceRange(Double minPrice, Double maxPrice) {
        return enrichedOrderRepository.findByItemPriceBetween(minPrice, maxPrice);
    }

    // Find orders by multiple statuses
    public List<EnrichedOrder> findEnrichedOrdersByStatuses(List<String> statuses) {
        return enrichedOrderRepository.findByOrderStatusIn(statuses);
    }

    // Update enriched order status with transaction support
    @Transactional
    public boolean updateEnrichedOrderStatus(String orderId, String newStatus) {
        Query query = new Query(Criteria.where("orderId").is(orderId));
        Update update = new Update().set("orderStatus", newStatus).set("processedTime", LocalDateTime.now());
        
        return mongoTemplate.updateFirst(query, update, EnrichedOrder.class).getModifiedCount() > 0;
    }

    // Update enriched order with transaction support
    @Transactional
    public boolean updateEnrichedOrder(String orderId, EnrichedOrder updatedOrder) {
        Query query = new Query(Criteria.where("orderId").is(orderId));
        Update update = new Update()
                .set("customerName", updatedOrder.getCustomerName())
                .set("itemName", updatedOrder.getItemName())
                .set("itemPrice", updatedOrder.getItemPrice())
                .set("totalAmount", updatedOrder.getTotalAmount())
                .set("orderStatus", updatedOrder.getOrderStatus())
                .set("processedTime", LocalDateTime.now());
        
        return mongoTemplate.updateFirst(query, update, EnrichedOrder.class).getModifiedCount() > 0;
    }

    // Delete enriched order with transaction support
    @Transactional
    public boolean deleteEnrichedOrder(String orderId) {
        Query query = new Query(Criteria.where("orderId").is(orderId));
        return mongoTemplate.remove(query, EnrichedOrder.class).getDeletedCount() > 0;
    }

    // Delete enriched orders by user ID with transaction support
    @Transactional
    public long deleteEnrichedOrdersByUserId(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        return mongoTemplate.remove(query, EnrichedOrder.class).getDeletedCount();
    }

    // Get statistics
    public long countEnrichedOrdersByStatus(String status) {
        return enrichedOrderRepository.countByOrderStatus(status);
    }

    public long countEnrichedOrdersByUserId(String userId) {
        return enrichedOrderRepository.countByUserId(userId);
    }

    public long countAllEnrichedOrders() {
        return enrichedOrderRepository.count();
    }

    // Advanced search with multiple criteria
    public List<EnrichedOrder> searchEnrichedOrders(String userId, String itemName, String status, 
                                                   LocalDateTime startDate, LocalDateTime endDate) {
        Query query = new Query();
        
        if (userId != null && !userId.trim().isEmpty()) {
            query.addCriteria(Criteria.where("userId").is(userId));
        }
        
        if (itemName != null && !itemName.trim().isEmpty()) {
            query.addCriteria(Criteria.where("itemName").regex(itemName, "i"));
        }
        
        if (status != null && !status.trim().isEmpty()) {
            query.addCriteria(Criteria.where("orderStatus").is(status));
        }
        
        if (startDate != null && endDate != null) {
            query.addCriteria(Criteria.where("orderTime").gte(startDate).lte(endDate));
        }
        
        return mongoTemplate.find(query, EnrichedOrder.class);
    }

    // Bulk operations with transaction support
    @Transactional
    public void bulkUpdateOrderStatuses(List<String> orderIds, String newStatus) {
        Query query = new Query(Criteria.where("orderId").in(orderIds));
        Update update = new Update().set("orderStatus", newStatus).set("processedTime", LocalDateTime.now());
        mongoTemplate.updateMulti(query, update, EnrichedOrder.class);
    }

    // Check if enriched order exists
    public boolean existsEnrichedOrder(String orderId) {
        return enrichedOrderRepository.existsById(orderId);
    }
} 