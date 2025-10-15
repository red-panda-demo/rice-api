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
public class RiceOrderResponse {
    private String orderId;
    private CustomerDto customer;
    private List<OrderItemDto> orderItems;
    private DeliveryAddressDto deliveryAddress;
    private String status;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryTime;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private Integer itemCount;  // Additional field for convenience
}

