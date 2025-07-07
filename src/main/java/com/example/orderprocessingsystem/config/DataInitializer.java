package com.example.orderprocessingsystem.config;

import com.example.orderprocessingsystem.model.Order;
import com.example.orderprocessingsystem.model.OrderItem;
import com.example.orderprocessingsystem.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(OrderRepository orderRepository) {
        return args -> {
            // Create sample orders
            Order order1 = new Order();
            order1.setCustomerName("John Doe");
            order1.setCustomerEmail("john.doe@example.com");
            order1.setShippingAddress("123 Main St, Anytown, USA");
            order1.setStatus(Order.OrderStatus.PENDING);
            order1.setOrderDate(LocalDateTime.now().minusDays(2));
            order1.setTotalAmount(new BigDecimal("129.99"));

            // Create order items for order1
            OrderItem item1 = new OrderItem();
            item1.setProductName("Smartphone");
            item1.setProductCode("TECH-1001");
            item1.setQuantity(1);
            item1.setUnitPrice(new BigDecimal("99.99"));
            item1.calculateSubtotal();

            OrderItem item2 = new OrderItem();
            item2.setProductName("Phone Case");
            item2.setProductCode("ACC-2001");
            item2.setQuantity(1);
            item2.setUnitPrice(new BigDecimal("30.00"));
            item2.calculateSubtotal();

            // Add items to order
            order1.addItem(item1);
            order1.addItem(item2);

            // Create another sample order
            Order order2 = new Order();
            order2.setCustomerName("Jane Smith");
            order2.setCustomerEmail("jane.smith@example.com");
            order2.setShippingAddress("456 Oak Ave, Somewhere, USA");
            order2.setStatus(Order.OrderStatus.PROCESSED);
            order2.setOrderDate(LocalDateTime.now().minusDays(7));
            order2.setTotalAmount(new BigDecimal("75.50"));

            // Create order items for order2
            OrderItem item3 = new OrderItem();
            item3.setProductName("Headphones");
            item3.setProductCode("TECH-3001");
            item3.setQuantity(1);
            item3.setUnitPrice(new BigDecimal("75.50"));
            item3.calculateSubtotal();

            // Add item to order
            order2.addItem(item3);

            // Save orders to database
            orderRepository.saveAll(Arrays.asList(order1, order2));

            System.out.println("Sample data initialized!");
        };
    }
}