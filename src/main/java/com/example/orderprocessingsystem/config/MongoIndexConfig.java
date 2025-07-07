package com.example.orderprocessingsystem.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class MongoIndexConfig {

    @Bean
    @Order(1) // Run before data initialization
    public CommandLineRunner createMongoIndexes(MongoClient mongoClient) {
        return args -> {
            MongoCollection<Document> enrichedOrderCollection = mongoClient
                .getDatabase("orderDB")
                .getCollection("enrichedOrder");

            // Create indexes for better query performance
            try {
                // Index on orderId (unique)
                enrichedOrderCollection.createIndex(Indexes.ascending("orderId"));
                System.out.println("Created index on orderId");

                // Index on userId for user-specific queries
                enrichedOrderCollection.createIndex(Indexes.ascending("userId"));
                System.out.println("Created index on userId");

                // Index on orderStatus for status-based queries
                enrichedOrderCollection.createIndex(Indexes.ascending("orderStatus"));
                System.out.println("Created index on orderStatus");

                // Index on orderTime for date range queries
                enrichedOrderCollection.createIndex(Indexes.ascending("orderTime"));
                System.out.println("Created index on orderTime");

                // Compound index on userId and orderStatus
                enrichedOrderCollection.createIndex(Indexes.compoundIndex(
                    Indexes.ascending("userId"),
                    Indexes.ascending("orderStatus")
                ));
                System.out.println("Created compound index on userId and orderStatus");

                // Compound index on userId and orderTime
                enrichedOrderCollection.createIndex(Indexes.compoundIndex(
                    Indexes.ascending("userId"),
                    Indexes.ascending("orderTime")
                ));
                System.out.println("Created compound index on userId and orderTime");

                // Text index on itemName for text search
                enrichedOrderCollection.createIndex(Indexes.text("itemName"));
                System.out.println("Created text index on itemName");

                // Text index on customerName for text search
                enrichedOrderCollection.createIndex(Indexes.text("customerName"));
                System.out.println("Created text index on customerName");

                // Index on totalAmount for amount-based queries
                enrichedOrderCollection.createIndex(Indexes.ascending("totalAmount"));
                System.out.println("Created index on totalAmount");

                // Index on itemPrice for price range queries
                enrichedOrderCollection.createIndex(Indexes.ascending("itemPrice"));
                System.out.println("Created index on itemPrice");

                // Index on processedTime for processing time queries
                enrichedOrderCollection.createIndex(Indexes.ascending("processedTime"));
                System.out.println("Created index on processedTime");

                System.out.println("All MongoDB indexes created successfully!");

            } catch (Exception e) {
                System.err.println("Error creating MongoDB indexes: " + e.getMessage());
            }
        };
    }
} 