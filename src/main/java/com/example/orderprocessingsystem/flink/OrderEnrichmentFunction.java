package com.example.orderprocessingsystem.flink;

import com.example.orderprocessingsystem.model.EnrichedOrder;
import com.example.orderprocessingsystem.model.OrderEvent;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.time.LocalDateTime;

public class OrderEnrichmentFunction extends KeyedProcessFunction<String, OrderEvent, EnrichedOrder> {
    
    @Override
    public void processElement(OrderEvent order, Context ctx, Collector<EnrichedOrder> out) {
        EnrichedOrder enriched = new EnrichedOrder(order, "PREMIUM_USER", LocalDateTime.now());
        out.collect(enriched);
    }
} 