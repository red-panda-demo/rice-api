package com.example.riceapi.controller;

import com.example.riceapi.dto.*;
import com.example.riceapi.mapper.RiceOrderMapper;
import com.example.riceapi.modal.RiceOrder;
import com.example.riceapi.repository.RiceOrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
public class RiceOrderController {
    
    private final RiceOrderRepository repository;
    private final RiceOrderMapper mapper;
    
    public RiceOrderController(RiceOrderRepository repository, RiceOrderMapper mapper) {
        this.repository = repository;
        
        this.mapper = mapper;
    }
    
    /**
     * GET /api/v1/orders - Retrieve all orders
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RiceOrderResponse>>> getAllOrders() {
        List<RiceOrder> orders = repository.getAllOrders();
        List<RiceOrderResponse> response = orders.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(
                ApiResponse.success(response, "Retrieved " + response.size() + " orders successfully")
        );
    }
}

