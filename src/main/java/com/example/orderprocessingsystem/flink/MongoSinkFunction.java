package com.example.orderprocessingsystem.flink;

import com.example.orderprocessingsystem.model.EnrichedOrder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.bson.Document;

public class MongoSinkFunction extends RichSinkFunction<EnrichedOrder> {
    private transient MongoCollection<Document> collection;

    @Override
    public void open(Configuration parameters) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase db = mongoClient.getDatabase("orderDB");
        collection = db.getCollection("enrichedOrders");
    }

    @Override
    public void invoke(EnrichedOrder value, Context context) {
        Document doc = new Document("orderId", value.getOrderId())
            .append("userId", value.getUserId())
            .append("status", value.getOrderStatus())
            .append("timestamp", value.getProcessedTime());
        collection.insertOne(doc);
    }
} 