package com.example.riceapi.modal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddress {
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String additionalInstructions;
}

