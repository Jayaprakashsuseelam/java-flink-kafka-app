package com.example.orderprocessingsystem.service;

import com.example.orderprocessingsystem.model.EnrichedOrder;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MongoTransactionService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private MongoOrderService mongoOrderService;

    /**
     * Transfer orders between users with transaction support
     */
    public boolean transferOrdersBetweenUsers(String fromUserId, String toUserId, List<String> orderIds) {
        try (ClientSession session = mongoClient.startSession()) {
            return session.withTransaction(() -> {
                MongoCollection<Document> collection = mongoClient
                    .getDatabase("orderDB")
                    .getCollection("enrichedOrder");

                // Update all orders to transfer ownership
                for (String orderId : orderIds) {
                    collection.updateOne(
                        session,
                        Filters.eq("orderId", orderId),
                        Updates.combine(
                            Updates.set("userId", toUserId),
                            Updates.set("processedTime", LocalDateTime.now())
                        )
                    );
                }
                return true;
            });
        } catch (Exception e) {
            System.err.println("Error in transfer transaction: " + e.getMessage());
            return false;
        }
    }

    /**
     * Bulk status update with transaction support
     */
    public boolean bulkStatusUpdateWithValidation(List<String> orderIds, String newStatus, String userId) {
        try (ClientSession session = mongoClient.startSession()) {
            return session.withTransaction(() -> {
                MongoCollection<Document> collection = mongoClient
                    .getDatabase("orderDB")
                    .getCollection("enrichedOrder");

                // Validate that all orders belong to the user
                for (String orderId : orderIds) {
                    Document order = collection.find(session, Filters.eq("orderId", orderId)).first();
                    if (order == null || !userId.equals(order.getString("userId"))) {
                        throw new RuntimeException("Order " + orderId + " not found or doesn't belong to user " + userId);
                    }
                }

                // Update all orders
                for (String orderId : orderIds) {
                    collection.updateOne(
                        session,
                        Filters.eq("orderId", orderId),
                        Updates.combine(
                            Updates.set("orderStatus", newStatus),
                            Updates.set("processedTime", LocalDateTime.now())
                        )
                    );
                }
                return true;
            });
        } catch (Exception e) {
            System.err.println("Error in bulk status update transaction: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create order with inventory check transaction
     */
    public boolean createOrderWithInventoryCheck(EnrichedOrder enrichedOrder, int availableQuantity) {
        try (ClientSession session = mongoClient.startSession()) {
            return session.withTransaction(() -> {
                MongoCollection<Document> collection = mongoClient
                    .getDatabase("orderDB")
                    .getCollection("enrichedOrder");

                // Check if order already exists
                Document existingOrder = collection.find(session, Filters.eq("orderId", enrichedOrder.getOrderId())).first();
                if (existingOrder != null) {
                    throw new RuntimeException("Order " + enrichedOrder.getOrderId() + " already exists");
                }

                // Check inventory availability
                if (enrichedOrder.getQuantity() > availableQuantity) {
                    throw new RuntimeException("Insufficient inventory. Available: " + availableQuantity + ", Requested: " + enrichedOrder.getQuantity());
                }

                // Create the order
                Document orderDoc = new Document()
                    .append("orderId", enrichedOrder.getOrderId())
                    .append("userId", enrichedOrder.getUserId())
                    .append("itemId", enrichedOrder.getItemId())
                    .append("quantity", enrichedOrder.getQuantity())
                    .append("orderTime", enrichedOrder.getOrderTime())
                    .append("customerName", enrichedOrder.getCustomerName())
                    .append("itemName", enrichedOrder.getItemName())
                    .append("itemPrice", enrichedOrder.getItemPrice())
                    .append("totalAmount", enrichedOrder.getTotalAmount())
                    .append("orderStatus", enrichedOrder.getOrderStatus())
                    .append("processedTime", enrichedOrder.getProcessedTime());

                collection.insertOne(session, orderDoc);
                return true;
            });
        } catch (Exception e) {
            System.err.println("Error in create order transaction: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete user orders with cleanup transaction
     */
    public boolean deleteUserOrdersWithCleanup(String userId) {
        try (ClientSession session = mongoClient.startSession()) {
            return session.withTransaction(() -> {
                MongoCollection<Document> collection = mongoClient
                    .getDatabase("orderDB")
                    .getCollection("enrichedOrder");

                // Count orders before deletion
                long orderCount = collection.countDocuments(session, Filters.eq("userId", userId));

                // Delete all orders for the user
                collection.deleteMany(session, Filters.eq("userId", userId));

                // Log the cleanup
                System.out.println("Cleaned up " + orderCount + " orders for user " + userId);

                return orderCount > 0;
            });
        } catch (Exception e) {
            System.err.println("Error in delete user orders transaction: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update order with audit trail transaction
     */
    public boolean updateOrderWithAuditTrail(String orderId, EnrichedOrder updatedOrder) {
        try (ClientSession session = mongoClient.startSession()) {
            return session.withTransaction(() -> {
                MongoCollection<Document> collection = mongoClient
                    .getDatabase("orderDB")
                    .getCollection("enrichedOrder");

                // Get the original order
                Document originalOrder = collection.find(session, Filters.eq("orderId", orderId)).first();
                if (originalOrder == null) {
                    throw new RuntimeException("Order " + orderId + " not found");
                }

                // Create audit trail
                Document auditTrail = new Document()
                    .append("orderId", orderId)
                    .append("originalData", originalOrder)
                    .append("updatedData", new Document()
                        .append("customerName", updatedOrder.getCustomerName())
                        .append("itemName", updatedOrder.getItemName())
                        .append("itemPrice", updatedOrder.getItemPrice())
                        .append("totalAmount", updatedOrder.getTotalAmount())
                        .append("orderStatus", updatedOrder.getOrderStatus())
                        .append("processedTime", LocalDateTime.now()))
                    .append("updateTime", LocalDateTime.now());

                // Insert audit trail
                mongoClient.getDatabase("orderDB")
                    .getCollection("auditTrail")
                    .insertOne(session, auditTrail);

                // Update the order
                collection.updateOne(
                    session,
                    Filters.eq("orderId", orderId),
                    Updates.combine(
                        Updates.set("customerName", updatedOrder.getCustomerName()),
                        Updates.set("itemName", updatedOrder.getItemName()),
                        Updates.set("itemPrice", updatedOrder.getItemPrice()),
                        Updates.set("totalAmount", updatedOrder.getTotalAmount()),
                        Updates.set("orderStatus", updatedOrder.getOrderStatus()),
                        Updates.set("processedTime", LocalDateTime.now())
                    )
                );

                return true;
            });
        } catch (Exception e) {
            System.err.println("Error in update order transaction: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get transaction statistics
     */
    public Document getTransactionStatistics() {
        try (ClientSession session = mongoClient.startSession()) {
            return session.withTransaction(() -> {
                MongoCollection<Document> collection = mongoClient
                    .getDatabase("orderDB")
                    .getCollection("enrichedOrder");

                Document stats = new Document();
                stats.put("totalOrders", collection.countDocuments(session));
                stats.put("pendingOrders", collection.countDocuments(session, Filters.eq("orderStatus", "PENDING")));
                stats.put("processedOrders", collection.countDocuments(session, Filters.eq("orderStatus", "PROCESSED")));
                stats.put("cancelledOrders", collection.countDocuments(session, Filters.eq("orderStatus", "CANCELLED")));
                stats.put("lastUpdated", LocalDateTime.now());

                return stats;
            });
        } catch (Exception e) {
            System.err.println("Error getting transaction statistics: " + e.getMessage());
            return new Document("error", e.getMessage());
        }
    }
} 