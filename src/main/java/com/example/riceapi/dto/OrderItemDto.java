package com.example.riceapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private String itemId;
    private String riceType;
    private Integer quantity;
    private BigDecimal pricePerUnit;
    private String spiceLevel;
    private String additionalNotes;
}

