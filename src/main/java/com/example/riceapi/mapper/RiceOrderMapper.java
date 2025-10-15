package com.example.riceapi.mapper;

import com.example.riceapi.dto.*;
import com.example.riceapi.modal.Customer;
import com.example.riceapi.modal.DeliveryAddress;
import com.example.riceapi.modal.OrderItem;
import com.example.riceapi.modal.RiceOrder;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RiceOrderMapper {
    
    public RiceOrder toEntity(RiceOrderRequest request) {
        if (request == null) {
            return null;
        }
        
        return RiceOrder.builder()
                .orderId(request.getOrderId())
                .customer(toCustomerEntity(request.getCustomer()))
                .orderItems(request.getOrderItems() != null ? 
                        request.getOrderItems().stream()
                                .map(this::toOrderItemEntity)
                                .collect(Collectors.toList()) : null)
                .deliveryAddress(toDeliveryAddressEntity(request.getDeliveryAddress()))
                .status(request.getStatus() != null ? 
                        RiceOrder.OrderStatus.valueOf(request.getStatus()) : null)
                .orderDate(request.getOrderDate())
                .deliveryTime(request.getDeliveryTime())
                .paymentMethod(request.getPaymentMethod())
                .totalAmount(request.getTotalAmount())
                .build();
    }
    
    public RiceOrderResponse toResponse(RiceOrder entity) {
        if (entity == null) {
            return null;
        }
        
        return RiceOrderResponse.builder()
                .orderId(entity.getOrderId())
                .customer(toCustomerDto(entity.getCustomer()))
                .orderItems(entity.getOrderItems() != null ? 
                        entity.getOrderItems().stream()
                                .map(this::toOrderItemDto)
                                .collect(Collectors.toList()) : null)
                .deliveryAddress(toDeliveryAddressDto(entity.getDeliveryAddress()))
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .orderDate(entity.getOrderDate())
                .deliveryTime(entity.getDeliveryTime())
                .paymentMethod(entity.getPaymentMethod())
                .totalAmount(entity.getTotalAmount())
                .itemCount(entity.getOrderItems() != null ? entity.getOrderItems().size() : 0)
                .build();
    }
    
    private Customer toCustomerEntity(CustomerDto dto) {
        if (dto == null) {
            return null;
        }
        return Customer.builder()
                .customerId(dto.getCustomerId())
                .name(dto.getName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .build();
    }
    
    private CustomerDto toCustomerDto(Customer entity) {
        if (entity == null) {
            return null;
        }
        return CustomerDto.builder()
                .customerId(entity.getCustomerId())
                .name(entity.getName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .build();
    }
    
    private OrderItem toOrderItemEntity(OrderItemDto dto) {
        if (dto == null) {
            return null;
        }
        return OrderItem.builder()
                .itemId(dto.getItemId())
                .riceType(dto.getRiceType())
                .quantity(dto.getQuantity())
                .pricePerUnit(dto.getPricePerUnit())
                .spiceLevel(dto.getSpiceLevel())
                .additionalNotes(dto.getAdditionalNotes())
                .build();
    }
    
    private OrderItemDto toOrderItemDto(OrderItem entity) {
        if (entity == null) {
            return null;
        }
        return OrderItemDto.builder()
                .itemId(entity.getItemId())
                .riceType(entity.getRiceType())
                .quantity(entity.getQuantity())
                .pricePerUnit(entity.getPricePerUnit())
                .spiceLevel(entity.getSpiceLevel())
                .additionalNotes(entity.getAdditionalNotes())
                .build();
    }
    
    private DeliveryAddress toDeliveryAddressEntity(DeliveryAddressDto dto) {
        if (dto == null) {
            return null;
        }
        return DeliveryAddress.builder()
                .street(dto.getStreet())
                .city(dto.getCity())
                .state(dto.getState())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .additionalInstructions(dto.getAdditionalInstructions())
                .build();
    }
    
    private DeliveryAddressDto toDeliveryAddressDto(DeliveryAddress entity) {
        if (entity == null) {
            return null;
        }
        return DeliveryAddressDto.builder()
                .street(entity.getStreet())
                .city(entity.getCity())
                .state(entity.getState())
                .postalCode(entity.getPostalCode())
                .country(entity.getCountry())
                .additionalInstructions(entity.getAdditionalInstructions())
                .build();
    }
}

