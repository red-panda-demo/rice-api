package com.example.riceapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiceOrderRequest {
    private String orderId;
    private CustomerDto customer;
    private List<OrderItemDto> orderItems;
    private DeliveryAddressDto deliveryAddress;
    private String status;  // String representation of OrderStatus enum
    private LocalDateTime orderDate;
    private LocalDateTime deliveryTime;
    private String paymentMethod;
    private BigDecimal totalAmount;
}

