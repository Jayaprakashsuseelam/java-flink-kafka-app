# Order Processing System

A comprehensive real-time order processing system with Kafka, Apache Flink, and MongoDB integration.

## Project Overview

This application provides a complete real-time order processing pipeline that includes:
- **Spring Boot REST API** for order management
- **Apache Kafka** for event streaming
- **Apache Flink** for real-time stream processing and enrichment
- **MongoDB** for storing enriched order data
- **H2 Database** for traditional order management

## Features

### Core Order Management
- Create new orders with multiple order items
- Retrieve orders by ID, customer email, status, or date range
- Update existing orders and order status
- Delete orders

### Real-Time Stream Processing
- **Kafka Integration**: Publish order events to Kafka topics
- **Flink Stream Processing**: Real-time enrichment of order data
- **MongoDB Storage**: Store enriched orders for analytics
- **REST API**: Query enriched orders from MongoDB

## Technologies Used

- **Java 11**
- **Spring Boot 2.7.5**
- **Spring Data JPA & MongoDB**
- **Apache Kafka** (Spring Kafka)
- **Apache Flink 1.17.2**
- **MongoDB 4.11.1**
- **H2 Database** (in-memory)
- **Lombok**
- **Maven**

## Project Structure

```
src/main/java/com/example/orderprocessingsystem/
├── config/
│   ├── DataInitializer.java
│   └── KafkaConfig.java
├── controller/
│   ├── OrderController.java
│   └── EnrichedOrderController.java
├── model/
│   ├── Order.java
│   ├── OrderItem.java
│   ├── OrderEvent.java
│   └── EnrichedOrder.java
├── flink/
│   ├── OrderStreamProcessor.java
│   ├── OrderEnrichmentFunction.java
│   ├── OrderEventDeserializationSchema.java
│   └── MongoSinkFunction.java
├── repository/
│   ├── OrderRepository.java
│   └── OrderItemRepository.java
├── service/
│   ├── OrderService.java
│   └── impl/
│       └── OrderServiceImpl.java
└── OrderProcessingSystemApplication.java
```

## Getting Started

### Prerequisites

- **Java 11** or higher
- **Maven**
- **Apache Kafka** running on `localhost:9092`
- **MongoDB** running on `localhost:27017`

### Running the Application

1. **Start Kafka** (ensure topic "orders" exists)
2. **Start MongoDB** on localhost:27017
3. **Clone the repository**
4. **Navigate to the project directory**
5. **Run the Spring Boot application:**
   ```bash
   mvn spring-boot:run
   ```
6. **Run the Flink stream processor:**
   ```bash
   mvn exec:java -Dexec.mainClass="com.example.orderprocessingsystem.flink.OrderStreamProcessor"
   ```

### Application Ports

- **Spring Boot Application**: `http://localhost:8085`
- **H2 Database Console**: `http://localhost:8085/h2-console`
- **Kafka**: `localhost:9092`
- **MongoDB**: `localhost:27017`

### Accessing the H2 Database Console

- URL: http://localhost:8085/h2-console
- JDBC URL: jdbc:h2:mem:orderdb
- Username: sa
- Password: (leave empty)

## API Endpoints

### Order Management API

#### Orders (H2 Database)
- `POST /api/orders` - Create a new order
- `GET /api/orders` - Get all orders
- `GET /api/orders/{id}` - Get order by ID
- `PUT /api/orders/{id}` - Update an order
- `DELETE /api/orders/{id}` - Delete an order
- `GET /api/orders/email/{email}` - Get orders by customer email
- `GET /api/orders/status/{status}` - Get orders by status
- `GET /api/orders/date-range?startDate=...&endDate=...` - Get orders by date range
- `PATCH /api/orders/{id}/status?status=...` - Update order status

#### Kafka Order Events
- `POST /api/orders/place-order` - Place order event to Kafka topic "orders"

#### Enriched Orders (MongoDB)
- `GET /api/enriched-orders/{userId}` - Get enriched orders by user ID

## Real-Time Processing Pipeline

### 1. Order Event Publishing
```bash
# Publish order event to Kafka
curl -X POST http://localhost:8085/api/orders/place-order \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "order123",
    "userId": "user456",
    "itemId": "item789",
    "quantity": 2,
    "orderTime": "2024-01-15T10:30:00"
  }'
```

### 2. Flink Stream Processing
The Flink application:
- Consumes from Kafka topic "orders"
- Enriches orders with "PREMIUM_USER" status
- Stores enriched data in MongoDB collection "enrichedOrders"

### 3. Query Enriched Data
```bash
# Query enriched orders by user ID
curl http://localhost:8085/api/enriched-orders/user456
```

## Configuration

### Kafka Configuration
```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
```

### MongoDB Configuration
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/orderDB
```

### Server Configuration
```properties
server.port=8085
```

## Sample Data

The application is initialized with sample data for testing purposes. Two orders with different statuses and items are created when the application starts.

## Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Spring Boot   │    │   Apache Kafka  │    │   Apache Flink  │
│   REST API      │───▶│   Topic: orders │───▶│   Stream Proc.  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                                                │
         │                                                ▼
         │                                        ┌─────────────────┐
         │                                        │     MongoDB     │
         │                                        │ enrichedOrders  │
         └────────────────────────────────────────┼─────────────────┘
                                                  │
                                                  ▼
                                         ┌─────────────────┐
                                         │   REST API      │
                                         │ Query Enriched  │
                                         │     Orders      │
                                         └─────────────────┘
```
