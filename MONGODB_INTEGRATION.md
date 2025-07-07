# MongoDB Integration and Transaction Management

This document describes the MongoDB integration and transaction management features implemented in the Order Processing System.

## Overview

The application now includes comprehensive MongoDB integration with:
- MongoDB configuration with connection pooling
- Repository pattern for data access
- Transaction support for complex operations
- Index optimization for better performance
- Audit trail functionality
- Bulk operations with validation

## Architecture

### Components

1. **MongoConfig** - MongoDB configuration with connection settings
2. **MongoOrderService** - Service layer for enriched order operations
3. **MongoTransactionService** - Transaction management for complex operations
4. **EnrichedOrderMongoRepository** - Repository interface with custom queries
5. **MongoDataInitializer** - Sample data initialization
6. **MongoIndexConfig** - Database index optimization

## Configuration

### Application Properties

```properties
# MongoDB configuration
spring.data.mongodb.uri=mongodb://localhost:27017/orderDB
spring.data.mongodb.database=orderDB
spring.data.mongodb.auto-index-creation=true

# Connection pool settings
spring.data.mongodb.connection-pool.max-size=100
spring.data.mongodb.connection-pool.min-size=5
spring.data.mongodb.connection-pool.max-wait-time=5000

# Write concern and read preference
spring.data.mongodb.write-concern=MAJORITY
spring.data.mongodb.read-preference=PRIMARY
spring.data.mongodb.transaction-support=REPLICA_SET
```

## API Endpoints

### Enriched Orders API (`/api/enriched-orders`)

#### Basic Operations
- `GET /{userId}` - Get orders by user ID
- `GET /order/{orderId}` - Get order by ID
- `GET /status/{status}` - Get orders by status
- `GET /date-range` - Get orders by date range
- `POST /` - Create new enriched order
- `PUT /{orderId}` - Update enriched order
- `DELETE /{orderId}` - Delete enriched order

#### Advanced Queries
- `GET /min-amount/{minAmount}` - Get orders with minimum amount
- `GET /item/{itemName}` - Get orders by item name
- `GET /customer/{customerName}` - Get orders by customer name
- `GET /top-orders` - Get top orders by amount
- `GET /recent` - Get recent orders
- `GET /price-range` - Get orders by price range
- `GET /search` - Advanced search with multiple criteria

#### Bulk Operations
- `POST /bulk` - Create multiple orders
- `PATCH /bulk-status` - Bulk status update
- `DELETE /user/{userId}` - Delete all user orders

#### Statistics
- `GET /stats` - Get overall statistics
- `GET /stats/{userId}` - Get user-specific statistics
- `GET /health` - Health check

### MongoDB Transactions API (`/api/mongo-transactions`)

#### Transaction Operations
- `POST /transfer-orders` - Transfer orders between users
- `PATCH /bulk-status-update` - Bulk status update with validation
- `POST /create-order-with-inventory` - Create order with inventory check
- `DELETE /delete-user-orders/{userId}` - Delete user orders with cleanup
- `PUT /update-order-with-audit/{orderId}` - Update order with audit trail
- `GET /statistics` - Get transaction statistics
- `GET /health` - Transaction service health check

## Database Indexes

The following indexes are automatically created for optimal performance:

### Single Field Indexes
- `orderId` - For unique order lookups
- `userId` - For user-specific queries
- `orderStatus` - For status-based filtering
- `orderTime` - For date range queries
- `totalAmount` - For amount-based queries
- `itemPrice` - For price range queries
- `processedTime` - For processing time queries

### Compound Indexes
- `userId + orderStatus` - For user status queries
- `userId + orderTime` - For user date range queries

### Text Indexes
- `itemName` - For text search on item names
- `customerName` - For text search on customer names

## Transaction Features

### 1. Order Transfer Between Users
```bash
POST /api/mongo-transactions/transfer-orders
?fromUserId=user001&toUserId=user002
Body: ["ORD-001", "ORD-002"]
```

### 2. Bulk Status Update with Validation
```bash
PATCH /api/mongo-transactions/bulk-status-update
?orderIds=ORD-001,ORD-002&newStatus=PROCESSED&userId=user001
```

### 3. Create Order with Inventory Check
```bash
POST /api/mongo-transactions/create-order-with-inventory
?availableQuantity=10
Body: {
  "orderId": "ORD-011",
  "userId": "user001",
  "quantity": 2,
  "itemName": "Laptop",
  "itemPrice": 1299.99
}
```

### 4. Update Order with Audit Trail
```bash
PUT /api/mongo-transactions/update-order-with-audit/ORD-001
Body: {
  "customerName": "John Doe Updated",
  "orderStatus": "PROCESSED"
}
```

## Data Models

### EnrichedOrder
```java
public class EnrichedOrder {
    private String orderId;
    private String userId;
    private String itemId;
    private int quantity;
    private LocalDateTime orderTime;
    private String customerName;
    private String itemName;
    private Double itemPrice;
    private Double totalAmount;
    private String orderStatus;
    private LocalDateTime processedTime;
}
```

## Sample Data

The system automatically initializes with sample enriched orders:
- 10 sample orders across 5 users
- Various order statuses (PENDING, PROCESSED, CANCELLED)
- Different item types and price ranges
- Realistic customer names and order times

## Error Handling

The MongoDB integration includes comprehensive error handling:
- Connection timeout handling
- Transaction rollback on failures
- Validation errors with descriptive messages
- Audit trail for failed operations

## Performance Optimization

### Connection Pooling
- Maximum 100 connections
- Minimum 5 connections
- 5-second connection timeout

### Query Optimization
- Indexed queries for common operations
- Compound indexes for multi-field queries
- Text indexes for search functionality

### Transaction Management
- Session-based transactions
- Automatic rollback on errors
- Optimistic locking for concurrent updates

## Monitoring and Health Checks

### Health Endpoints
- `/api/enriched-orders/health` - Enriched orders service health
- `/api/mongo-transactions/health` - Transaction service health

### Statistics Endpoints
- `/api/enriched-orders/stats` - Overall order statistics
- `/api/mongo-transactions/statistics` - Transaction statistics

## Setup Instructions

1. **Install MongoDB**
   ```bash
   # For Windows
   # Download and install MongoDB Community Server
   
   # For Linux/Mac
   sudo apt-get install mongodb
   ```

2. **Start MongoDB**
   ```bash
   # Start MongoDB service
   sudo systemctl start mongod
   
   # Or start manually
   mongod --dbpath /data/db
   ```

3. **Verify Connection**
   ```bash
   # Test MongoDB connection
   mongo --eval "db.runCommand('ping')"
   ```

4. **Run Application**
   ```bash
   # Build and run the Spring Boot application
   mvn clean install
   java -jar target/order-processing-system-0.0.1-SNAPSHOT.jar
   ```

## Testing the Integration

### Test Basic Operations
```bash
# Get orders for a user
curl http://localhost:8085/api/enriched-orders/user001

# Create a new order
curl -X POST http://localhost:8085/api/enriched-orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-011","userId":"user001","itemName":"Test Item"}'

# Get statistics
curl http://localhost:8085/api/enriched-orders/stats
```

### Test Transactions
```bash
# Transfer orders between users
curl -X POST "http://localhost:8085/api/mongo-transactions/transfer-orders?fromUserId=user001&toUserId=user002" \
  -H "Content-Type: application/json" \
  -d '["ORD-001", "ORD-002"]'

# Bulk status update
curl -X PATCH "http://localhost:8085/api/mongo-transactions/bulk-status-update?orderIds=ORD-001,ORD-002&newStatus=PROCESSED&userId=user001"
```

## Troubleshooting

### Common Issues

1. **MongoDB Connection Failed**
   - Verify MongoDB is running
   - Check connection string in application.properties
   - Ensure network connectivity

2. **Transaction Errors**
   - MongoDB must be running as a replica set for transactions
   - Check MongoDB version (4.0+ required for transactions)

3. **Index Creation Errors**
   - Indexes are created automatically on startup
   - Check MongoDB logs for index creation errors

4. **Performance Issues**
   - Monitor query performance with MongoDB profiler
   - Check index usage with `explain()` queries
   - Optimize slow queries

### Logs and Monitoring

Enable debug logging for MongoDB operations:
```properties
logging.level.org.springframework.data.mongodb=DEBUG
logging.level.com.mongodb=DEBUG
```

## Security Considerations

1. **Authentication**
   - Configure MongoDB authentication
   - Use environment variables for credentials

2. **Network Security**
   - Restrict MongoDB access to application servers
   - Use VPN for remote connections

3. **Data Encryption**
   - Enable MongoDB encryption at rest
   - Use TLS for network communication

## Future Enhancements

1. **Sharding Support**
   - Implement MongoDB sharding for horizontal scaling
   - Configure shard keys for optimal distribution

2. **Change Streams**
   - Implement real-time data synchronization
   - Use change streams for event-driven updates

3. **Aggregation Pipelines**
   - Add complex analytics queries
   - Implement data aggregation for reporting

4. **Backup and Recovery**
   - Automated backup strategies
   - Point-in-time recovery capabilities 