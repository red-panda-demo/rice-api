package com.example.riceapi.controller;

import com.example.riceapi.dto.*;
import com.example.riceapi.mapper.RiceOrderMapper;
import com.example.riceapi.modal.RiceOrder;
import com.example.riceapi.repository.RiceOrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RiceOrderController.class)
@DisplayName("RiceOrderController Unit Tests")
class RiceOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // For converting DTOs to JSON

    @MockBean
    private RiceOrderRepository repository;

    @MockBean
    private RiceOrderMapper mapper;

    // --- Mock Data ---
    private RiceOrder order1;
    private RiceOrder order2;
    private RiceOrderRequest orderRequest1;
    private RiceOrderRequest orderRequest2;
    private RiceOrderResponse orderResponse1;
    private RiceOrderResponse orderResponse2;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.of(2023, 1, 1, 10, 0, 0);

        order1 = new RiceOrder("order1", "customer1", "productA", 2, RiceOrder.OrderStatus.PENDING, now);
        order2 = new RiceOrder("order2", "customer2", "productB", 1, RiceOrder.OrderStatus.DELIVERED, now.plusHours(1));

        orderRequest1 = new RiceOrderRequest("order1", "customer1", "productA", 2, "PENDING");
        orderRequest2 = new RiceOrderRequest("order2", "customer2", "productB", 1, "DELIVERED");

        orderResponse1 = new RiceOrderResponse("order1", "customer1", "productA", 2, "PENDING", now);
        orderResponse2 = new RiceOrderResponse("order2", "customer2", "productB", 1, "DELIVERED", now.plusHours(1));
    }

    // --- GET /api/v1/orders - Retrieve all orders ---
    @Test
    @DisplayName("shouldReturnAllOrders_whenOrdersExist")
    void shouldReturnAllOrders_whenOrdersExist_thenStatus200AndListOfOrders() throws Exception {
        List<RiceOrder> orders = List.of(order1, order2);
        List<RiceOrderResponse> responses = List.of(orderResponse1, orderResponse2);

        when(repository.getAllOrders()).thenReturn(orders);
        when(mapper.toResponse(order1)).thenReturn(orderResponse1);
        when(mapper.toResponse(order2)).thenReturn(orderResponse2);

        mockMvc.perform(get("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].orderId").value("order1"))
                .andExpect(jsonPath("$.data[1].orderId").value("order2"))
                .andExpect(jsonPath("$.message").value("Retrieved 2 orders successfully"));

        verify(repository, times(1)).getAllOrders();
        verify(mapper, times(1)).toResponse(order1);
        verify(mapper, times(1)).toResponse(order2);
    }

    @Test
    @DisplayName("shouldReturnEmptyList_whenNoOrdersExist")
    void shouldReturnEmptyList_whenNoOrdersExist_thenStatus200AndEmptyList() throws Exception {
        when(repository.getAllOrders()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.message").value("Retrieved 0 orders successfully"));

        verify(repository, times(1)).getAllOrders();
        verify(mapper, never()).toResponse(any(RiceOrder.class));
    }

    // --- GET /api/v1/orders/{orderId} - Retrieve a single order by ID ---
    @Test
    @DisplayName("shouldReturnOrder_whenOrderFoundById")
    void shouldReturnOrder_whenOrderFoundById_thenStatus200AndOrder() throws Exception {
        when(repository.getOrderById("order1")).thenReturn(Optional.of(order1));
        when(mapper.toResponse(order1)).thenReturn(orderResponse1);

        mockMvc.perform(get("/api/v1/orders/{orderId}", "order1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value("order1"))
                .andExpect(jsonPath("$.message").value("Order retrieved successfully"));

        verify(repository, times(1)).getOrderById("order1");
        verify(mapper, times(1)).toResponse(order1);
    }

    @Test
    @DisplayName("shouldReturnNotFound_whenOrderNotFoundById")
    void shouldReturnNotFound_whenOrderNotFoundById_thenStatus404() throws Exception {
        when(repository.getOrderById("nonexistentId")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/orders/{orderId}", "nonexistentId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Order with ID nonexistentId not found"));

        verify(repository, times(1)).getOrderById("nonexistentId");
        verify(mapper, never()).toResponse(any(RiceOrder.class));
    }

    // --- GET /api/v1/orders/status/{status} - Retrieve orders by status ---
    @Test
    @DisplayName("shouldReturnOrders_whenValidStatusAndOrdersExist")
    void shouldReturnOrders_whenValidStatusAndOrdersExist_thenStatus200AndListOfOrders() throws Exception {
        List<RiceOrder> orders = List.of(order1); // Only order1 is PENDING
        List<RiceOrderResponse> responses = List.of(orderResponse1);

        when(repository.getOrdersByStatus(RiceOrder.OrderStatus.PENDING)).thenReturn(orders);
        when(mapper.toResponse(order1)).thenReturn(orderResponse1);

        mockMvc.perform(get("/api/v1/orders/status/{status}", "PENDING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].orderId").value("order1"))
                .andExpect(jsonPath("$.message").value("Retrieved 1 orders with status: PENDING"));

        verify(repository, times(1)).getOrdersByStatus(RiceOrder.OrderStatus.PENDING);
        verify(mapper, times(1)).toResponse(order1);
    }

    @Test
    @DisplayName("shouldReturnEmptyList_whenValidStatusAndNoOrdersExist")
    void shouldReturnEmptyList_whenValidStatusAndNoOrdersExist_thenStatus200AndEmptyList() throws Exception {
        when(repository.getOrdersByStatus(RiceOrder.OrderStatus.CONFIRMED)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/orders/status/{status}", "CONFIRMED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.message").value("Retrieved 0 orders with status: CONFIRMED"));

        verify(repository, times(1)).getOrdersByStatus(RiceOrder.OrderStatus.CONFIRMED);
        verify(mapper, never()).toResponse(any(RiceOrder.class));
    }

    @Test
    @DisplayName("shouldReturnBadRequest_whenInvalidStatus")
    void shouldReturnBadRequest_whenInvalidStatus_thenStatus400() throws Exception {
        String invalidStatus = "INVALID_STATUS";

        mockMvc.perform(get("/api/v1/orders/status/{status}", invalidStatus)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error", containsString("Invalid status: " + invalidStatus)));

        verify(repository, never()).getOrdersByStatus(any(RiceOrder.OrderStatus.class));
        verify(mapper, never()).toResponse(any(RiceOrder.class));
    }

    // --- GET /api/v1/orders/customer/{customerId} - Retrieve orders by customer ID ---
    @Test
    @DisplayName("shouldReturnOrders_whenOrdersFoundByCustomerId")
    void shouldReturnOrders_whenOrdersFoundByCustomerId_thenStatus200AndListOfOrders() throws Exception {
        List<RiceOrder> orders = List.of(order1);
        List<RiceOrderResponse> responses = List.of(orderResponse1);

        when(repository.getOrdersByCustomerId("customer1")).thenReturn(orders);
        when(mapper.toResponse(order1)).thenReturn(orderResponse1);

        mockMvc.perform(get("/api/v1/orders/customer/{customerId}", "customer1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].orderId").value("order1"))
                .andExpect(jsonPath("$.message").value("Retrieved 1 orders for customer: customer1"));

        verify(repository, times(1)).getOrdersByCustomerId("customer1");
        verify(mapper, times(1)).toResponse(order1);
    }

    @Test
    @DisplayName("shouldReturnEmptyList_whenNoOrdersFoundByCustomerId")
    void shouldReturnEmptyList_whenNoOrdersFoundByCustomerId_thenStatus200AndEmptyList() throws Exception {
        when(repository.getOrdersByCustomerId("nonexistentCustomer")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/orders/customer/{customerId}", "nonexistentCustomer")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.message").value("Retrieved 0 orders for customer: nonexistentCustomer"));

        verify(repository, times(1)).getOrdersByCustomerId("nonexistentCustomer");
        verify(mapper, never()).toResponse(any(RiceOrder.class));
    }

    // --- POST /api/v1/orders - Add a single order ---
    @Test
    @DisplayName("shouldCreateOrder_whenValidRequest")
    void shouldCreateOrder_whenValidRequest_thenStatus201AndOrderResponse() throws Exception {
        when(mapper.toEntity(orderRequest1)).thenReturn(order1);
        when(repository.addOrder(order1)).thenReturn(order1);
        when(mapper.toResponse(order1)).thenReturn(orderResponse1);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value("order1"))
                .andExpect(jsonPath("$.message").value("Order created successfully with ID: order1"));

        verify(mapper, times(1)).toEntity(orderRequest1);
        verify(repository, times(1)).addOrder(order1);
        verify(mapper, times(1)).toResponse(order1);
    }

    @Test
    @DisplayName("shouldReturnBadRequest_whenIllegalArgumentExceptionFromService")
    void shouldReturnBadRequest_whenIllegalArgumentExceptionFromService_thenStatus400() throws Exception {
        when(mapper.toEntity(any(RiceOrderRequest.class))).thenThrow(new IllegalArgumentException("Invalid order data"));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Failed to create order: Invalid order data"));

        verify(mapper, times(1)).toEntity(orderRequest1);
        verify(repository, never()).addOrder(any(RiceOrder.class));
        verify(mapper, never()).toResponse(any(RiceOrder.class));
    }

    @Test
    @DisplayName("shouldReturnInternalServerError_whenGenericExceptionFromService")
    void shouldReturnInternalServerError_whenGenericExceptionFromService_thenStatus500() throws Exception {
        when(mapper.toEntity(any(RiceOrderRequest.class))).thenReturn(order1);
        when(repository.addOrder(any(RiceOrder.class))).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest1)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("An error occurred while creating the order: Database error"));

        verify(mapper, times(1)).toEntity(orderRequest1);
        verify(repository, times(1)).addOrder(order1);
        verify(mapper, never()).toResponse(any(RiceOrder.class));
    }

    // --- POST /api/v1/orders/batch - Add multiple orders ---
    @Test
    @DisplayName("shouldCreateMultipleOrders_whenAllRequestsAreValid")
    void shouldCreateMultipleOrders_whenAllRequestsAreValid_thenStatus201AndListOfOrderResponses() throws Exception {
        BatchOrderRequest batchRequest = new BatchOrderRequest(List.of(orderRequest1, orderRequest2));

        when(mapper.toEntity(orderRequest1)).thenReturn(order1);
        when(mapper.toEntity(orderRequest2)).thenReturn(order2);
        when(repository.addOrder(order1)).thenReturn(order1);
        when(repository.addOrder(order2)).thenReturn(order2);
        when(mapper.toResponse(order1)).thenReturn(orderResponse1);
        when(mapper.toResponse(order2)).thenReturn(orderResponse2);

        mockMvc.perform(post("/api/v1/orders/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].orderId").value("order1"))
                .andExpect(jsonPath("$.data[1].orderId").value("order2"))
                .andExpect(jsonPath("$.message").value("Successfully added 2 out of 2 orders"));

        verify(mapper, times(1)).toEntity(orderRequest1);
        verify(mapper, times(1)).toEntity(orderRequest2);
        verify(repository, times(1)).addOrder(order1);
        verify(repository, times(1)).addOrder(order2);
        verify(mapper, times(1)).toResponse(order1);
        verify(mapper, times(1)).toResponse(order2);
    }

    @Test
    @DisplayName("shouldReturnPartialSuccess_whenSomeOrdersFail")
    void shouldReturnPartialSuccess_whenSomeOrdersFail_thenStatus201AndPartialSuccessMessage() throws Exception {
        BatchOrderRequest batchRequest = new BatchOrderRequest(List.of(orderRequest1, orderRequest2));

        // orderRequest1 succeeds, orderRequest2 fails
        when(mapper.toEntity(orderRequest1)).thenReturn(order1);
        when(repository.addOrder(order1)).thenReturn(order1);
        when(mapper.toResponse(order1)).thenReturn(orderResponse1);

        when(mapper.toEntity(orderRequest2)).thenThrow(new IllegalArgumentException("Invalid data for order2")); // Simulate failure for order2

        mockMvc.perform(post("/api/v1/orders/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isCreated()) // Still 201 because some succeeded
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].orderId").value("order1"))
                .andExpect(jsonPath("$.message", containsString("Successfully added 1 out of 2 orders")))
                .andExpect(jsonPath("$.message", containsString("Errors: Failed to add order order2: Invalid data for order2")));

        verify(mapper, times(1)).toEntity(orderRequest1);
        verify(mapper, times(1)).toEntity(orderRequest2);
        verify(repository, times(1)).addOrder(order1);
        verify(repository, never()).addOrder(order2); // addOrder for order2 should not be called if toEntity fails
        verify(mapper, times(1)).toResponse(order1);
    }

    @Test
    @DisplayName("shouldReturnBadRequest_whenAllOrdersFail")
    void shouldReturnBadRequest_whenAllOrdersFail_thenStatus400AndErrorMessage() throws Exception {
        BatchOrderRequest batchRequest = new BatchOrderRequest(List.of(orderRequest1, orderRequest2));

        // Both orders fail
        when(mapper.toEntity(orderRequest1)).thenThrow(new IllegalArgumentException("Invalid data for order1"));
        when(mapper.toEntity(orderRequest2)).thenThrow(new IllegalArgumentException("Invalid data for order2"));

        mockMvc.perform(post("/api/v1/orders/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isBadRequest()) // 400 because no orders succeeded
                .andExpect(jsonPath("$.success").value(true)) // The controller returns success=true even if all fail in batch
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.message", containsString("Successfully added 0 out of 2 orders")))
                .andExpect(jsonPath("$.message", containsString("Errors: Failed to add order order1: Invalid data for order1")))
                .andExpect(jsonPath("$.message", containsString("Failed to add order order2: Invalid data for order2")));

        verify(mapper, times(1)).toEntity(orderRequest1);
        verify(mapper, times(1)).toEntity(orderRequest2);
        verify(repository, never()).addOrder(any(RiceOrder.class));
        verify(mapper, never()).toResponse(any(RiceOrder.class));
    }

    @Test
    @DisplayName("shouldReturnBadRequest_whenBatchRequestIsNull")
    void shouldReturnBadRequest_whenBatchRequestIsNull_thenStatus400() throws Exception {
        mockMvc.perform(post("/api/v1/orders/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null))) // Sending null
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Request body must contain a list of orders"));

        verifyNoInteractions(repository, mapper);
    }

    @Test
    @DisplayName("shouldReturnBadRequest_whenBatchRequestHasEmptyOrderList")
    void shouldReturnBadRequest_whenBatchRequestHasEmptyOrderList_thenStatus400() throws Exception {
        BatchOrderRequest emptyBatchRequest = new BatchOrderRequest(Collections.emptyList());

        mockMvc.perform(post("/api/v1/orders/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyBatchRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Request body must contain a list of orders"));

        verifyNoInteractions(repository, mapper);
    }

    // --- PUT /api/v1/orders/{orderId} - Update an existing order (full update) ---
    @Test
    @DisplayName("shouldUpdateOrder_whenOrderExistsAndValidRequest")
    void shouldUpdateOrder_whenOrderExistsAndValidRequest_thenStatus200AndUpdatedOrder() throws Exception {
        RiceOrder updatedOrderEntity = new RiceOrder("order1", "customer1", "productC", 3, RiceOrder.OrderStatus.CONFIRMED, order1.getOrderDate());
        RiceOrderRequest updateRequest = new RiceOrderRequest("order1", "customer1", "productC", 3, "CONFIRMED");
        RiceOrderResponse updatedOrderResponse = new RiceOrderResponse("order1", "customer1", "productC", 3, "CONFIRMED", order1.getOrderDate());

        when(mapper.toEntity(updateRequest)).thenReturn(updatedOrderEntity);
        when(repository.updateOrder("order1", updatedOrderEntity)).thenReturn(Optional.of(updatedOrderEntity));
        when(mapper.toResponse(updatedOrderEntity)).thenReturn(updatedOrderResponse);

        mockMvc.perform(put("/api/v1/orders/{orderId}", "order1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value("order1"))
                .andExpect(jsonPath("$.data.productName").value("productC"))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.message").value("Order updated successfully"));

        verify(mapper, times(1)).toEntity(updateRequest);
        verify(repository, times(1)).updateOrder("order1", updatedOrderEntity);
        verify(mapper, times(1)).toResponse(updatedOrderEntity);
    }

    @Test
    @DisplayName("shouldReturnNotFound_whenOrderToUpdateNotFound")
    void shouldReturnNotFound_whenOrderToUpdateNotFound_thenStatus404() throws Exception {
        RiceOrder updatedOrderEntity = new RiceOrder("nonexistentId", "customer1", "productC", 3, RiceOrder.OrderStatus.CONFIRMED, order1.getOrderDate());
        RiceOrderRequest updateRequest = new RiceOrderRequest("nonexistentId", "customer1", "productC", 3, "CONFIRMED");

        when(mapper.toEntity(updateRequest)).thenReturn(updatedOrderEntity);
        when(repository.updateOrder("nonexistentId", updatedOrderEntity)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/orders/{orderId}", "nonexistentId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Order with ID nonexistentId not found"));

        verify(mapper, times(1)).toEntity(updateRequest);
        verify(repository, times(1)).updateOrder("nonexistentId", updatedOrderEntity);
        verify(mapper, never()).toResponse(any(RiceOrder.class));
    }

    @Test
    @DisplayName("shouldReturnBadRequest_whenIllegalArgumentExceptionOnUpdate")
    void shouldReturnBadRequest_whenIllegalArgumentExceptionOnUpdate_thenStatus400() throws Exception {
        RiceOrderRequest updateRequest = new RiceOrderRequest("order1", "customer1", "productC", 3, "INVALID_STATUS");
        when(mapper.toEntity(updateRequest)).thenThrow(new IllegalArgumentException("Invalid status value"));

        mockMvc.perform(put("/api/v1/orders/{orderId}", "order1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Failed to update order: Invalid status value"));

        verify(mapper, times(1)).toEntity(updateRequest);
        verify(repository, never()).updateOrder(anyString(), any(RiceOrder.class));
        verify(mapper, never()).toResponse(any(RiceOrder.class));
    }

    @Test
    @DisplayName("shouldReturnInternalServerError_whenGenericExceptionOnUpdate")
    void shouldReturnInternalServerError_whenGenericExceptionOnUpdate_thenStatus500() throws Exception {
        RiceOrder updatedOrderEntity = new RiceOrder("order1", "customer1", "productC", 3, RiceOrder.OrderStatus.CONFIRMED, order1.getOrderDate());
        RiceOrderRequest updateRequest = new RiceOrderRequest("order1", "customer1", "productC", 3, "CONFIRMED");

        when(mapper.toEntity(updateRequest)).thenReturn(updatedOrderEntity);
        when(repository.updateOrder("order1", updatedOrderEntity)).thenThrow(new RuntimeException("DB connection lost"));

        mockMvc.perform(put("/api/v1/orders/{orderId}", "order1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("An error occurred while updating the order: DB connection lost"));

        verify(mapper, times(1)).toEntity(updateRequest);
        verify(repository, times(1)).updateOrder("order1", updatedOrderEntity);
        verify(mapper, never()).toResponse(any(RiceOrder.class));
    }

    // --- PATCH /api/v1/orders/{orderId} - Partially update an existing order ---
    @Test
    @DisplayName("shouldPartiallyUpdateOrder_whenOrderExistsAndValidRequest")
    void shouldPartiallyUpdateOrder_whenOrderExistsAndValidRequest_thenStatus200AndUpdatedOrder() throws Exception {
        RiceOrder partialUpdatesEntity = new RiceOrder(null, null, null, 0, RiceOrder.OrderStatus.CONFIRMED, null); // Simulate partial update with only status
        RiceOrderRequest partialUpdateRequest = new RiceOrderRequest(null, null, null, null, "CONFIRMED"); // Only status provided in request
        RiceOrder updatedOrderFromRepo = new RiceOrder("order1", "customer1", "productA", 2, RiceOrder.OrderStatus.CONFIRMED, order1.getOrderDate()); // The full order after partial update
        RiceOrderResponse updatedOrderResponse = new RiceOrderResponse("order1", "customer1", "productA", 2, "CONFIRMED", order1.getOrderDate());

        when(mapper.toEntity(partialUpdateRequest)).thenReturn(partialUpdatesEntity);
        when(repository.partialUpdateOrder("order1", partialUpdatesEntity)).thenReturn(Optional.of(updatedOrderFromRepo));
        when(mapper.toResponse(updatedOrderFromRepo)).thenReturn(updatedOrderResponse);

        mockMvc.perform(patch("/api/v1/orders/{orderId}", "order1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value("order1"))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.message").value("Order partially updated successfully"));

        verify(mapper, times(1)).toEntity(partialUpdateRequest);
        verify(repository, times(1)).partialUpdateOrder("order1", partialUpdatesEntity);
        verify(mapper, times(1)).toResponse(updatedOrderFromRepo);
    }

    @Test
    @DisplayName("shouldReturnNotFound_whenOrderToPartialUpdateNotFound")
    void shouldReturnNotFound_whenOrderToPartialUpdateNotFound_thenStatus404() throws Exception {
        RiceOrder partialUpdatesEntity = new RiceOrder(null, null, null, 0, RiceOrder.OrderStatus.CONFIRMED, null);
        RiceOrderRequest partialUpdateRequest = new RiceOrderRequest(null, null, null, null, "CONFIRMED");

        when(mapper.toEntity(partialUpdateRequest)).thenReturn(partialUpdatesEntity);
        when(repository.partialUpdateOrder("nonexistentId", partialUpdatesEntity)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/v1/orders/{orderId}", "nonexistentId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Order with ID nonexistentId not found"));

        verify(mapper, times(1)).toEntity(partialUpdateRequest);
        verify(repository, times(1)).partialUpdateOrder("nonexistentId", partialUpdatesEntity);
        verify(mapper, never()).toResponse(any(RiceOrder.class));
    }

    @Test
    @DisplayName("shouldReturnBadRequest_whenIllegalArgumentExceptionOnPartialUpdate")
    void shouldReturnBadRequest_whenIllegalArgumentExceptionOnPartialUpdate_thenStatus400() throws Exception {
        RiceOrderRequest partialUpdateRequest = new RiceOrderRequest(null, null, null, null, "INVALID_STATUS");
        when(mapper.toEntity(partialUpdateRequest)).thenThrow(new IllegalArgumentException("Invalid status value for partial update"));

        mockMvc.perform(patch("/api/v1/orders/{orderId}", "order1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Failed to update order: Invalid status value for partial update"));

        verify(mapper, times(1)).toEntity(partialUpdateRequest);
        verify(repository, never()).partialUpdateOrder(anyString(), any(RiceOrder.class));
        verify(mapper, never()).toResponse(any(RiceOrder.class));
    }

    @Test
    @DisplayName("shouldReturnInternalServerError_whenGenericExceptionOnPartialUpdate")
    void shouldReturnInternalServerError_whenGenericExceptionOnPartialUpdate_thenStatus500() throws Exception {
        RiceOrder partialUpdatesEntity = new RiceOrder(null, null, null, 0, RiceOrder.OrderStatus.CONFIRMED, null);
        RiceOrderRequest partialUpdateRequest = new RiceOrderRequest(null, null, null, null, "CONFIRMED");

        when(mapper.toEntity(partialUpdateRequest)).thenReturn(partialUpdatesEntity);
        when(repository.partialUpdateOrder("order1", partialUpdatesEntity)).thenThrow(new RuntimeException("DB connectivity issue during partial update"));

        mockMvc.perform(patch("/api/v1/orders/{orderId}", "order1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdateRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("An error occurred while updating the order: DB connectivity issue during partial update"));

        verify(mapper, times(1)).toEntity(partialUpdateRequest);
        verify(repository, times(1)).partialUpdateOrder("order1", partialUpdatesEntity);
        verify(mapper, never()).toResponse(any(RiceOrder.class));
    }

    // --- DELETE /api/v1/orders/{orderId} - Delete an order by ID ---
    @Test
    @DisplayName("shouldDeleteOrder_whenOrderExists")
    void shouldDeleteOrder_whenOrderExists_thenStatus200() throws Exception {
        when(repository.removeOrder("order1")).thenReturn(true);

        mockMvc.perform(delete("/api/v1/orders/{orderId}", "order1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").doesNotExist()) // No data expected for delete
                .andExpect(jsonPath("$.message").value("Order deleted successfully"));

        verify(repository, times(1)).removeOrder("order1");
    }

    @Test
    @DisplayName("shouldReturnNotFound_whenOrderToDeleteNotFound")
    void shouldReturnNotFound_whenOrderToDeleteNotFound_thenStatus404() throws Exception {
        when(repository.removeOrder("nonexistentId")).thenReturn(false);

        mockMvc.perform(delete("/api/v1/orders/{orderId}", "nonexistentId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Order with ID nonexistentId not found"));

        verify(repository, times(1)).removeOrder("nonexistentId");
    }

    @Test
    @DisplayName("shouldReturnBadRequest_whenIllegalArgumentExceptionOnDelete")
    void shouldReturnBadRequest_whenIllegalArgumentExceptionOnDelete_thenStatus400() throws Exception {
        when(repository.removeOrder(anyString())).thenThrow(new IllegalArgumentException("Invalid ID format"));

        mockMvc.perform(delete("/api/v1/orders/{orderId}", "invalid-id-format")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Failed to delete order: Invalid ID format"));

        verify(repository, times(1)).removeOrder("invalid-id-format");
    }

    @Test
    @DisplayName("shouldReturnInternalServerError_whenGenericExceptionOnDelete")
    void shouldReturnInternalServerError_whenGenericExceptionOnDelete_thenStatus500() throws Exception {
        when(repository.removeOrder("order1")).thenThrow(new RuntimeException("Database error during delete"));

        mockMvc.perform(delete("/api/v1/orders/{orderId}", "order1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("An error occurred while deleting the order: Database error during delete"));

        verify(repository, times(1)).removeOrder("order1");
    }

    // --- DELETE /api/v1/orders - Delete all orders ---
    @Test
    @DisplayName("shouldDeleteAllOrders_whenCalled")
    void shouldDeleteAllOrders_whenCalled_thenStatus200() throws Exception {
        when(repository.getOrderCount()).thenReturn(2);
        doNothing().when(repository).removeAllOrders();

        mockMvc.perform(delete("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Successfully deleted 2 orders"));

        verify(repository, times(1)).getOrderCount();
        verify(repository, times(1)).removeAllOrders();
    }

    @Test
    @DisplayName("shouldReturnInternalServerError_whenGenericExceptionOnDeleteAll")
    void shouldReturnInternalServerError_whenGenericExceptionOnDeleteAll_thenStatus500() throws Exception {
        when(repository.getOrderCount()).thenReturn(0); // Can happen before exception or not
        doThrow(new RuntimeException("Failed to clear database")).when(repository).removeAllOrders();

        mockMvc.perform(delete("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("An error occurred while deleting orders: Failed to clear database"));

        verify(repository, times(1)).getOrderCount();
        verify(repository, times(1)).removeAllOrders();
    }
}