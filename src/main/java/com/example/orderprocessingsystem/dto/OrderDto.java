package com.example.orderprocessingsystem.dto;

import com.example.orderprocessingsystem.model.Order;
import com.example.orderprocessingsystem.model.OrderItem;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private String customerName;
    private String customerEmail;
    private String shippingAddress;
    private String status;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private List<OrderItemDto> items;

    public static OrderDto fromOrder(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setStatus(order.getStatus().name());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream()
                    .map(OrderItemDto::fromOrderItem)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
} 