package com.example.orderprocessingsystem.flink;

import com.example.orderprocessingsystem.model.EnrichedOrder;
import com.example.orderprocessingsystem.model.OrderEvent;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;

public class OrderStreamProcessor {

    public static void main(String[] args) throws Exception {
        
        // Set up the streaming execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        
        // Configure Kafka source
        KafkaSource<OrderEvent> kafkaSource = KafkaSource.<OrderEvent>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("orders")
                .setGroupId("order-processor-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setDeserializer(new OrderEventDeserializationSchema())
                .build();
        
        // Create the stream from Kafka
        DataStream<OrderEvent> stream = env.fromSource(kafkaSource, 
                WatermarkStrategy.noWatermarks(), 
                "Kafka Source");
        
        // Process and enrich the stream
        DataStream<EnrichedOrder> enrichedStream = stream
                .keyBy(OrderEvent::getUserId)
                .process(new OrderEnrichmentFunction());
        
        // Add MongoDB sink
        enrichedStream.addSink(new MongoSinkFunction());
        
        // Execute the job
        env.execute("Real-Time Order Processor");
    }
} 