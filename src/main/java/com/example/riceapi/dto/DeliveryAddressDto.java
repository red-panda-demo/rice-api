package com.example.riceapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddressDto {
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String additionalInstructions;
}

