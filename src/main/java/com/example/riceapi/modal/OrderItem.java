package com.example.riceapi.modal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private String itemId;
    private String riceType;  // e.g., "Nasi Goreng Special", "Nasi Goreng Ayam", "Nasi Goreng Seafood"
    private Integer quantity;
    private BigDecimal pricePerUnit;
    private String spiceLevel;  // e.g., "Mild", "Medium", "Hot", "Extra Hot"
    private String additionalNotes;
    
    public BigDecimal getSubtotal() {
        if (pricePerUnit == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return pricePerUnit.multiply(BigDecimal.valueOf(quantity));
    }
}

