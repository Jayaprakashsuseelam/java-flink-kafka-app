package com.example.orderprocessingsystem.flink;

import com.example.orderprocessingsystem.model.OrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

public class OrderEventDeserializationSchema implements KafkaRecordDeserializationSchema<OrderEvent> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<OrderEvent> collector) throws IOException {
        try {
            String jsonString = new String(record.value());
            OrderEvent orderEvent = objectMapper.readValue(jsonString, OrderEvent.class);
            collector.collect(orderEvent);
        } catch (Exception e) {
            // Log error and skip malformed records
            System.err.println("Error deserializing order event: " + e.getMessage());
        }
    }
    
    @Override
    public TypeInformation<OrderEvent> getProducedType() {
        return TypeInformation.of(OrderEvent.class);
    }
} 