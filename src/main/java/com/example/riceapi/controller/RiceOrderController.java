package com.example.riceapi.controller;

import com.example.riceapi.dto.*;
import com.example.riceapi.mapper.RiceOrderMapper;
import com.example.riceapi.modal.RiceOrder;
import com.example.riceapi.repository.RiceOrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    
    /**
     * GET /api/v1/orders/{orderId} - Retrieve a single order by ID
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<RiceOrderResponse>> getOrderById(@PathVariable String orderId) {
        Optional<RiceOrder> order = repository.getOrderById(orderId);
        
        if (order.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Order with ID " + orderId + " not found"));
        }
        
        return ResponseEntity.ok(
                ApiResponse.success(mapper.toResponse(order.get()), "Order retrieved successfully")
        );
    }
    
    /**
     * GET /api/v1/orders/status/{status} - Retrieve orders by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<RiceOrderResponse>>> getOrdersByStatus(@PathVariable String status) {
        try {
            RiceOrder.OrderStatus orderStatus = RiceOrder.OrderStatus.valueOf(status.toUpperCase());
            List<RiceOrder> orders = repository.getOrdersByStatus(orderStatus);
            List<RiceOrderResponse> response = orders.stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(
                    ApiResponse.success(response, "Retrieved " + response.size() + " orders with status: " + status)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid status: " + status + ". Valid values are: PENDING, CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED"));
        }
    }
    
    /**
     * GET /api/v1/orders/customer/{customerId} - Retrieve orders by customer ID
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<RiceOrderResponse>>> getOrdersByCustomerId(@PathVariable String customerId) {
        List<RiceOrder> orders = repository.getOrdersByCustomerId(customerId);
        List<RiceOrderResponse> response = orders.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(
                ApiResponse.success(response, "Retrieved " + response.size() + " orders for customer: " + customerId)
        );
    }
    
    /**
     * POST /api/v1/orders - Add a single order
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RiceOrderResponse>> addOrder(@RequestBody RiceOrderRequest request) {
        try {
            RiceOrder order = mapper.toEntity(request);
            RiceOrder savedOrder = repository.addOrder(order);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            mapper.toResponse(savedOrder),
                            "Order created successfully with ID: " + savedOrder.getOrderId()
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create order: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An error occurred while creating the order: " + e.getMessage()));
        }
    }
    
    /**
     * POST /api/v1/orders/batch - Add multiple orders
     */
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<RiceOrderResponse>>> addMultipleOrders(@RequestBody BatchOrderRequest batchRequest) {
        if (batchRequest == null || batchRequest.getOrders() == null || batchRequest.getOrders().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Request body must contain a list of orders"));
        }
        
        List<RiceOrderResponse> successfulOrders = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        for (RiceOrderRequest request : batchRequest.getOrders()) {
            try {
                RiceOrder order = mapper.toEntity(request);
                RiceOrder savedOrder = repository.addOrder(order);
                successfulOrders.add(mapper.toResponse(savedOrder));
            } catch (Exception e) {
                errors.add("Failed to add order " + request.getOrderId() + ": " + e.getMessage());
            }
        }
        
        String message = String.format("Successfully added %d out of %d orders", 
                successfulOrders.size(), batchRequest.getOrders().size());
        
        if (!errors.isEmpty()) {
            message += ". Errors: " + String.join("; ", errors);
        }
        
        HttpStatus status = successfulOrders.isEmpty() ? HttpStatus.BAD_REQUEST : HttpStatus.CREATED;
        
        return ResponseEntity.status(status)
                .body(ApiResponse.success(successfulOrders, message));
    }
    
    /**
     * PUT /api/v1/orders/{orderId} - Update an existing order (full update)
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponse<RiceOrderResponse>> updateOrder(
            @PathVariable String orderId,
            @RequestBody RiceOrderRequest request) {
        try {
            RiceOrder updatedOrder = mapper.toEntity(request);
            Optional<RiceOrder> result = repository.updateOrder(orderId, updatedOrder);
            
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Order with ID " + orderId + " not found"));
            }
            
            return ResponseEntity.ok(
                    ApiResponse.success(
                            mapper.toResponse(result.get()),
                            "Order updated successfully"
                    )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update order: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An error occurred while updating the order: " + e.getMessage()));
        }
    }
    
    /**
     * PATCH /api/v1/orders/{orderId} - Partially update an existing order
     */
    @PatchMapping("/{orderId}")
    public ResponseEntity<ApiResponse<RiceOrderResponse>> partialUpdateOrder(
            @PathVariable String orderId,
            @RequestBody RiceOrderRequest request) {
        try {
            RiceOrder updates = mapper.toEntity(request);
            Optional<RiceOrder> result = repository.partialUpdateOrder(orderId, updates);
            
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Order with ID " + orderId + " not found"));
            }
            
            return ResponseEntity.ok(
                    ApiResponse.success(
                            mapper.toResponse(result.get()),
                            "Order partially updated successfully"
                    )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update order: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An error occurred while updating the order: " + e.getMessage()));
        }
    }
    
    /**
     * DELETE /api/v1/orders/{orderId} - Delete an order by ID
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable String orderId) {
        try {
            boolean deleted = repository.removeOrder(orderId);
            
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Order with ID " + orderId + " not found"));
            }
            
            return ResponseEntity.ok(
                    ApiResponse.success(null, "Order deleted successfully")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete order: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An error occurred while deleting the order: " + e.getMessage()));
        }
    }
    
    /**
     * DELETE /api/v1/orders - Delete all orders
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteAllOrders() {
        try {
            int count = repository.getOrderCount();
            repository.removeAllOrders();
            
            return ResponseEntity.ok(
                    ApiResponse.success(null, "Successfully deleted " + count + " orders")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An error occurred while deleting orders: " + e.getMessage()));
        }
    }
}

