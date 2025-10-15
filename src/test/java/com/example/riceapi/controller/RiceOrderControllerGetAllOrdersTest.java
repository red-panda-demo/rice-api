package com.example.riceapi.controller;

import com.example.riceapi.dto.ApiResponse;
import com.example.riceapi.dto.CustomerDto;
import com.example.riceapi.dto.DeliveryAddressDto;
import com.example.riceapi.dto.OrderItemDto;
import com.example.riceapi.dto.RiceOrderResponse;
import com.example.riceapi.mapper.RiceOrderMapper;
import com.example.riceapi.modal.Customer;
import com.example.riceapi.modal.DeliveryAddress;
import com.example.riceapi.modal.OrderItem;
import com.example.riceapi.modal.RiceOrder;
import com.example.riceapi.repository.RiceOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RiceOrderController - getAllOrders() Tests")
class RiceOrderControllerGetAllOrdersTest {
    
    @Mock
    private RiceOrderRepository repository;
    
    @Mock
    private RiceOrderMapper mapper;
    
    @InjectMocks
    private RiceOrderController controller;
    
    private RiceOrder testOrder1;
    private RiceOrder testOrder2;
    private RiceOrder testOrder3;
    private RiceOrderResponse testResponse1;
    private RiceOrderResponse testResponse2;
    private RiceOrderResponse testResponse3;
    
    @BeforeEach
    void setUp() {
        // Setup test order 1
        Customer customer1 = Customer.builder()
                .customerId("CUST001")
                .name("John Doe")
                .email("john.doe@test.com")
                .phoneNumber("+1-555-0001")
                .build();
        
        DeliveryAddress address1 = DeliveryAddress.builder()
                .street("123 Main St")
                .city("New York")
                .state("NY")
                .postalCode("10001")
                .country("USA")
                .build();
        
        OrderItem item1 = OrderItem.builder()
                .itemId("ITEM001")
                .riceType("Nasi Goreng Special")
                .quantity(2)
                .pricePerUnit(new BigDecimal("50000"))
                .spiceLevel("Medium")
                .build();
        
        testOrder1 = RiceOrder.builder()
                .orderId("ORD001")
                .customer(customer1)
                .orderItems(Arrays.asList(item1))
                .deliveryAddress(address1)
                .status(RiceOrder.OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .deliveryTime(LocalDateTime.now().plusHours(1))
                .paymentMethod("Credit Card")
                .totalAmount(new BigDecimal("100000"))
                .build();
        
        // Setup test order 2
        Customer customer2 = Customer.builder()
                .customerId("CUST002")
                .name("Jane Smith")
                .email("jane.smith@test.com")
                .phoneNumber("+1-555-0002")
                .build();
        
        testOrder2 = RiceOrder.builder()
                .orderId("ORD002")
                .customer(customer2)
                .orderItems(Arrays.asList(item1))
                .deliveryAddress(address1)
                .status(RiceOrder.OrderStatus.CONFIRMED)
                .orderDate(LocalDateTime.now())
                .deliveryTime(LocalDateTime.now().plusHours(2))
                .paymentMethod("Cash")
                .totalAmount(new BigDecimal("100000"))
                .build();
        
        // Setup test order 3
        testOrder3 = RiceOrder.builder()
                .orderId("ORD003")
                .customer(customer1)
                .orderItems(Arrays.asList(item1))
                .deliveryAddress(address1)
                .status(RiceOrder.OrderStatus.DELIVERED)
                .orderDate(LocalDateTime.now().minusDays(1))
                .deliveryTime(LocalDateTime.now())
                .paymentMethod("E-Wallet")
                .totalAmount(new BigDecimal("100000"))
                .build();
        
        // Setup test responses
        CustomerDto customerDto1 = CustomerDto.builder()
                .customerId("CUST001")
                .name("John Doe")
                .email("john.doe@test.com")
                .phoneNumber("+1-555-0001")
                .build();
        
        CustomerDto customerDto2 = CustomerDto.builder()
                .customerId("CUST002")
                .name("Jane Smith")
                .email("jane.smith@test.com")
                .phoneNumber("+1-555-0002")
                .build();
        
        DeliveryAddressDto addressDto = DeliveryAddressDto.builder()
                .street("123 Main St")
                .city("New York")
                .state("NY")
                .postalCode("10001")
                .country("USA")
                .build();
        
        OrderItemDto itemDto = OrderItemDto.builder()
                .itemId("ITEM001")
                .riceType("Nasi Goreng Special")
                .quantity(2)
                .pricePerUnit(new BigDecimal("50000"))
                .spiceLevel("Medium")
                .build();
        
        testResponse1 = RiceOrderResponse.builder()
                .orderId("ORD001")
                .customer(customerDto1)
                .orderItems(Arrays.asList(itemDto))
                .deliveryAddress(addressDto)
                .status("PENDING")
                .totalAmount(new BigDecimal("100000"))
                .itemCount(1)
                .build();
        
        testResponse2 = RiceOrderResponse.builder()
                .orderId("ORD002")
                .customer(customerDto2)
                .orderItems(Arrays.asList(itemDto))
                .deliveryAddress(addressDto)
                .status("CONFIRMED")
                .totalAmount(new BigDecimal("100000"))
                .itemCount(1)
                .build();
        
        testResponse3 = RiceOrderResponse.builder()
                .orderId("ORD003")
                .customer(customerDto1)
                .orderItems(Arrays.asList(itemDto))
                .deliveryAddress(addressDto)
                .status("DELIVERED")
                .totalAmount(new BigDecimal("100000"))
                .itemCount(1)
                .build();
    }
    
    @Test
    @DisplayName("Should return all orders with HTTP 200 when orders exist")
    void shouldReturnAllOrdersWithSuccess() {
        // Arrange
        List<RiceOrder> orders = Arrays.asList(testOrder1, testOrder2, testOrder3);
        when(repository.getAllOrders()).thenReturn(orders);
        when(mapper.toResponse(testOrder1)).thenReturn(testResponse1);
        when(mapper.toResponse(testOrder2)).thenReturn(testResponse2);
        when(mapper.toResponse(testOrder3)).thenReturn(testResponse3);
        
        // Act
        ResponseEntity<ApiResponse<List<RiceOrderResponse>>> response = controller.getAllOrders();
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Retrieved 3 orders successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals(3, response.getBody().getData().size());
        
        // Verify the order of responses
        List<RiceOrderResponse> responseData = response.getBody().getData();
        assertEquals("ORD001", responseData.get(0).getOrderId());
        assertEquals("ORD002", responseData.get(1).getOrderId());
        assertEquals("ORD003", responseData.get(2).getOrderId());
        
        // Verify interactions
        verify(repository, times(1)).getAllOrders();
        verify(mapper, times(1)).toResponse(testOrder1);
        verify(mapper, times(1)).toResponse(testOrder2);
        verify(mapper, times(1)).toResponse(testOrder3);
    }
    
    @Test
    @DisplayName("Should return empty list with HTTP 200 when no orders exist")
    void shouldReturnEmptyListWhenNoOrders() {
        // Arrange
        List<RiceOrder> emptyOrders = Collections.emptyList();
        when(repository.getAllOrders()).thenReturn(emptyOrders);
        
        // Act
        ResponseEntity<ApiResponse<List<RiceOrderResponse>>> response = controller.getAllOrders();
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Retrieved 0 orders successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertTrue(response.getBody().getData().isEmpty());
        
        // Verify interactions
        verify(repository, times(1)).getAllOrders();
        verify(mapper, never()).toResponse(any(RiceOrder.class));
    }
    
    @Test
    @DisplayName("Should return single order when only one order exists")
    void shouldReturnSingleOrderWhenOnlyOneExists() {
        // Arrange
        List<RiceOrder> singleOrder = Arrays.asList(testOrder1);
        when(repository.getAllOrders()).thenReturn(singleOrder);
        when(mapper.toResponse(testOrder1)).thenReturn(testResponse1);
        
        // Act
        ResponseEntity<ApiResponse<List<RiceOrderResponse>>> response = controller.getAllOrders();
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Retrieved 1 orders successfully", response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().size());
        assertEquals("ORD001", response.getBody().getData().get(0).getOrderId());
        
        // Verify interactions
        verify(repository, times(1)).getAllOrders();
        verify(mapper, times(1)).toResponse(testOrder1);
    }
    
    @Test
    @DisplayName("Should map all orders correctly to response DTOs")
    void shouldMapAllOrdersCorrectly() {
        // Arrange
        List<RiceOrder> orders = Arrays.asList(testOrder1, testOrder2);
        when(repository.getAllOrders()).thenReturn(orders);
        when(mapper.toResponse(testOrder1)).thenReturn(testResponse1);
        when(mapper.toResponse(testOrder2)).thenReturn(testResponse2);
        
        // Act
        ResponseEntity<ApiResponse<List<RiceOrderResponse>>> response = controller.getAllOrders();
        
        // Assert
        assertNotNull(response.getBody());
        List<RiceOrderResponse> responseData = response.getBody().getData();
        
        // Verify first order mapping
        assertEquals("ORD001", responseData.get(0).getOrderId());
        assertEquals("CUST001", responseData.get(0).getCustomer().getCustomerId());
        assertEquals("John Doe", responseData.get(0).getCustomer().getName());
        assertEquals("PENDING", responseData.get(0).getStatus());
        assertEquals(new BigDecimal("100000"), responseData.get(0).getTotalAmount());
        
        // Verify second order mapping
        assertEquals("ORD002", responseData.get(1).getOrderId());
        assertEquals("CUST002", responseData.get(1).getCustomer().getCustomerId());
        assertEquals("Jane Smith", responseData.get(1).getCustomer().getName());
        assertEquals("CONFIRMED", responseData.get(1).getStatus());
        assertEquals(new BigDecimal("100000"), responseData.get(1).getTotalAmount());
        
        // Verify mapper was called for each order
        verify(mapper, times(1)).toResponse(testOrder1);
        verify(mapper, times(1)).toResponse(testOrder2);
    }
    
    @Test
    @DisplayName("Should return response with timestamp")
    void shouldReturnResponseWithTimestamp() {
        // Arrange
        List<RiceOrder> orders = Arrays.asList(testOrder1);
        when(repository.getAllOrders()).thenReturn(orders);
        when(mapper.toResponse(testOrder1)).thenReturn(testResponse1);
        
        LocalDateTime beforeCall = LocalDateTime.now();
        
        // Act
        ResponseEntity<ApiResponse<List<RiceOrderResponse>>> response = controller.getAllOrders();
        
        LocalDateTime afterCall = LocalDateTime.now();
        
        // Assert
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
        
        // Verify timestamp is between before and after the call
        LocalDateTime timestamp = response.getBody().getTimestamp();
        assertTrue(timestamp.isEqual(beforeCall) || timestamp.isAfter(beforeCall));
        assertTrue(timestamp.isEqual(afterCall) || timestamp.isBefore(afterCall));
    }
    
    @Test
    @DisplayName("Should handle large number of orders efficiently")
    void shouldHandleLargeNumberOfOrders() {
        // Arrange
        int numberOfOrders = 100;
        List<RiceOrder> largeOrderList = new ArrayList<>();
        List<RiceOrderResponse> largeResponseList = new ArrayList<>();
        
        for (int i = 0; i < numberOfOrders; i++) {
            RiceOrder order = RiceOrder.builder()
                    .orderId("ORD" + String.format("%03d", i))
                    .customer(testOrder1.getCustomer())
                    .orderItems(testOrder1.getOrderItems())
                    .deliveryAddress(testOrder1.getDeliveryAddress())
                    .status(RiceOrder.OrderStatus.PENDING)
                    .orderDate(LocalDateTime.now())
                    .totalAmount(new BigDecimal("50000"))
                    .build();
            largeOrderList.add(order);
            
            RiceOrderResponse response = RiceOrderResponse.builder()
                    .orderId("ORD" + String.format("%03d", i))
                    .status("PENDING")
                    .totalAmount(new BigDecimal("50000"))
                    .itemCount(1)
                    .build();
            largeResponseList.add(response);
            
            when(mapper.toResponse(order)).thenReturn(response);
        }
        
        when(repository.getAllOrders()).thenReturn(largeOrderList);
        
        // Act
        ResponseEntity<ApiResponse<List<RiceOrderResponse>>> response = controller.getAllOrders();
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Retrieved 100 orders successfully", response.getBody().getMessage());
        assertEquals(100, response.getBody().getData().size());
        
        // Verify repository was called once
        verify(repository, times(1)).getAllOrders();
        // Verify mapper was called exactly 100 times
        verify(mapper, times(100)).toResponse(any(RiceOrder.class));
    }
    
    @Test
    @DisplayName("Should maintain order of items returned from repository")
    void shouldMaintainOrderOfItems() {
        // Arrange
        List<RiceOrder> orderedList = Arrays.asList(testOrder3, testOrder1, testOrder2);
        when(repository.getAllOrders()).thenReturn(orderedList);
        when(mapper.toResponse(testOrder3)).thenReturn(testResponse3);
        when(mapper.toResponse(testOrder1)).thenReturn(testResponse1);
        when(mapper.toResponse(testOrder2)).thenReturn(testResponse2);
        
        // Act
        ResponseEntity<ApiResponse<List<RiceOrderResponse>>> response = controller.getAllOrders();
        
        // Assert
        assertNotNull(response.getBody());
        List<RiceOrderResponse> responseData = response.getBody().getData();
        
        // Verify order is maintained
        assertEquals("ORD003", responseData.get(0).getOrderId());
        assertEquals("ORD001", responseData.get(1).getOrderId());
        assertEquals("ORD002", responseData.get(2).getOrderId());
    }
    
    @Test
    @DisplayName("Should include all response fields correctly")
    void shouldIncludeAllResponseFieldsCorrectly() {
        // Arrange
        List<RiceOrder> orders = Arrays.asList(testOrder1);
        when(repository.getAllOrders()).thenReturn(orders);
        when(mapper.toResponse(testOrder1)).thenReturn(testResponse1);
        
        // Act
        ResponseEntity<ApiResponse<List<RiceOrderResponse>>> response = controller.getAllOrders();
        
        // Assert
        assertNotNull(response.getBody());
        
        // Verify ApiResponse structure
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertNotNull(response.getBody().getTimestamp());
        
        // Verify RiceOrderResponse structure
        RiceOrderResponse orderResponse = response.getBody().getData().get(0);
        assertNotNull(orderResponse.getOrderId());
        assertNotNull(orderResponse.getCustomer());
        assertNotNull(orderResponse.getOrderItems());
        assertNotNull(orderResponse.getDeliveryAddress());
        assertNotNull(orderResponse.getStatus());
        assertNotNull(orderResponse.getTotalAmount());
        assertNotNull(orderResponse.getItemCount());
    }
    
    @Test
    @DisplayName("Should call repository and mapper in correct sequence")
    void shouldCallServicesInCorrectSequence() {
        // Arrange
        List<RiceOrder> orders = Arrays.asList(testOrder1, testOrder2);
        when(repository.getAllOrders()).thenReturn(orders);
        when(mapper.toResponse(testOrder1)).thenReturn(testResponse1);
        when(mapper.toResponse(testOrder2)).thenReturn(testResponse2);
        
        // Act
        controller.getAllOrders();
        
        // Assert - verify order of invocations
        var inOrder = inOrder(repository, mapper);
        inOrder.verify(repository).getAllOrders();
        inOrder.verify(mapper).toResponse(testOrder1);
        inOrder.verify(mapper).toResponse(testOrder2);
        inOrder.verifyNoMoreInteractions();
    }
    
    @Test
    @DisplayName("Should not call mapper when repository returns empty list")
    void shouldNotCallMapperWhenEmptyList() {
        // Arrange
        when(repository.getAllOrders()).thenReturn(Collections.emptyList());
        
        // Act
        controller.getAllOrders();
        
        // Assert
        verify(repository, times(1)).getAllOrders();
        verifyNoInteractions(mapper);
    }
}

