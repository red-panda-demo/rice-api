package com.example.riceapi.repository;

import com.example.riceapi.modal.Customer;
import com.example.riceapi.modal.DeliveryAddress;
import com.example.riceapi.modal.OrderItem;
import com.example.riceapi.modal.RiceOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RiceOrderRepository Tests")
class RiceOrderRepositoryTest {
    
    private RiceOrderRepository repository;
    private RiceOrder testOrder1;
    private RiceOrder testOrder2;
    private RiceOrder testOrder3;
    
    @BeforeEach
    void setUp() {
        repository = new RiceOrderRepository();
        
        // Create test order 1
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
                .additionalInstructions("Ring doorbell")
                .build();
        
        OrderItem item1 = OrderItem.builder()
                .itemId("ITEM001")
                .riceType("Nasi Goreng Special")
                .quantity(2)
                .pricePerUnit(new BigDecimal("50000"))
                .spiceLevel("Medium")
                .additionalNotes("Extra shrimp")
                .build();
        
        testOrder1 = RiceOrder.builder()
                .orderId("TEST001")
                .customer(customer1)
                .orderItems(Arrays.asList(item1))
                .deliveryAddress(address1)
                .status(RiceOrder.OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .deliveryTime(LocalDateTime.now().plusHours(1))
                .paymentMethod("Credit Card")
                .totalAmount(new BigDecimal("100000"))
                .build();
        
        // Create test order 2
        Customer customer2 = Customer.builder()
                .customerId("CUST002")
                .name("Jane Smith")
                .email("jane.smith@test.com")
                .phoneNumber("+1-555-0002")
                .build();
        
        DeliveryAddress address2 = DeliveryAddress.builder()
                .street("456 Oak Ave")
                .city("Los Angeles")
                .state("CA")
                .postalCode("90001")
                .country("USA")
                .build();
        
        OrderItem item2 = OrderItem.builder()
                .itemId("ITEM002")
                .riceType("Nasi Goreng Ayam")
                .quantity(1)
                .pricePerUnit(new BigDecimal("35000"))
                .spiceLevel("Hot")
                .build();
        
        testOrder2 = RiceOrder.builder()
                .orderId("TEST002")
                .customer(customer2)
                .orderItems(Arrays.asList(item2))
                .deliveryAddress(address2)
                .status(RiceOrder.OrderStatus.CONFIRMED)
                .orderDate(LocalDateTime.now())
                .deliveryTime(LocalDateTime.now().plusHours(2))
                .paymentMethod("Cash")
                .totalAmount(new BigDecimal("35000"))
                .build();
        
        // Create test order 3 (same customer as order 1)
        testOrder3 = RiceOrder.builder()
                .orderId("TEST003")
                .customer(customer1)
                .orderItems(Arrays.asList(item1))
                .deliveryAddress(address1)
                .status(RiceOrder.OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .deliveryTime(LocalDateTime.now().plusHours(3))
                .paymentMethod("E-Wallet")
                .totalAmount(new BigDecimal("100000"))
                .build();
    }
    
    @Nested
    @DisplayName("Add Order Tests")
    class AddOrderTests {
        
        @Test
        @DisplayName("Should add a valid order successfully")
        void shouldAddValidOrder() {
            RiceOrder result = repository.addOrder(testOrder1);
            
            assertNotNull(result);
            assertEquals("TEST001", result.getOrderId());
            assertEquals(1, repository.getOrderCount());
        }
        
        @Test
        @DisplayName("Should set order date when adding order without date")
        void shouldSetOrderDateWhenMissing() {
            testOrder1.setOrderDate(null);
            RiceOrder result = repository.addOrder(testOrder1);
            
            assertNotNull(result.getOrderDate());
        }
        
        @Test
        @DisplayName("Should calculate total amount when adding order")
        void shouldCalculateTotalAmountWhenMissing() {
            testOrder1.setTotalAmount(null);
            RiceOrder result = repository.addOrder(testOrder1);
            
            assertNotNull(result.getTotalAmount());
            assertEquals(new BigDecimal("100000"), result.getTotalAmount());
        }
        
        @Test
        @DisplayName("Should throw exception when adding null order")
        void shouldThrowExceptionWhenAddingNullOrder() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                repository.addOrder(null);
            });
            
            assertEquals("Order cannot be null", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception when adding order with null ID")
        void shouldThrowExceptionWhenOrderIdIsNull() {
            testOrder1.setOrderId(null);
            
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                repository.addOrder(testOrder1);
            });
            
            assertEquals("Order ID cannot be null or empty", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception when adding order with empty ID")
        void shouldThrowExceptionWhenOrderIdIsEmpty() {
            testOrder1.setOrderId("");
            
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                repository.addOrder(testOrder1);
            });
            
            assertEquals("Order ID cannot be null or empty", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception when adding duplicate order ID")
        void shouldThrowExceptionWhenAddingDuplicateOrderId() {
            repository.addOrder(testOrder1);
            
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                repository.addOrder(testOrder1);
            });
            
            assertTrue(exception.getMessage().contains("already exists"));
        }
    }
    
    @Nested
    @DisplayName("Retrieve Order Tests")
    class RetrieveOrderTests {
        
        @BeforeEach
        void addTestOrders() {
            repository.addOrder(testOrder1);
            repository.addOrder(testOrder2);
            repository.addOrder(testOrder3);
        }
        
        @Test
        @DisplayName("Should retrieve all orders")
        void shouldRetrieveAllOrders() {
            List<RiceOrder> orders = repository.getAllOrders();
            
            assertNotNull(orders);
            assertEquals(3, orders.size());
        }
        
        @Test
        @DisplayName("Should retrieve order by ID")
        void shouldRetrieveOrderById() {
            Optional<RiceOrder> result = repository.getOrderById("TEST001");
            
            assertTrue(result.isPresent());
            assertEquals("TEST001", result.get().getOrderId());
            assertEquals("John Doe", result.get().getCustomer().getName());
        }
        
        @Test
        @DisplayName("Should return empty when order ID not found")
        void shouldReturnEmptyWhenOrderIdNotFound() {
            Optional<RiceOrder> result = repository.getOrderById("NONEXISTENT");
            
            assertTrue(result.isEmpty());
        }
        
        @Test
        @DisplayName("Should retrieve orders by status")
        void shouldRetrieveOrdersByStatus() {
            List<RiceOrder> pendingOrders = repository.getOrdersByStatus(RiceOrder.OrderStatus.PENDING);
            
            assertNotNull(pendingOrders);
            assertEquals(2, pendingOrders.size());
            assertTrue(pendingOrders.stream().allMatch(o -> o.getStatus() == RiceOrder.OrderStatus.PENDING));
        }
        
        @Test
        @DisplayName("Should return empty list when no orders match status")
        void shouldReturnEmptyListWhenNoOrdersMatchStatus() {
            List<RiceOrder> deliveredOrders = repository.getOrdersByStatus(RiceOrder.OrderStatus.DELIVERED);
            
            assertNotNull(deliveredOrders);
            assertTrue(deliveredOrders.isEmpty());
        }
        
        @Test
        @DisplayName("Should retrieve orders by customer ID")
        void shouldRetrieveOrdersByCustomerId() {
            List<RiceOrder> customerOrders = repository.getOrdersByCustomerId("CUST001");
            
            assertNotNull(customerOrders);
            assertEquals(2, customerOrders.size());
            assertTrue(customerOrders.stream()
                    .allMatch(o -> "CUST001".equals(o.getCustomer().getCustomerId())));
        }
        
        @Test
        @DisplayName("Should return empty list when customer has no orders")
        void shouldReturnEmptyListWhenCustomerHasNoOrders() {
            List<RiceOrder> customerOrders = repository.getOrdersByCustomerId("NONEXISTENT");
            
            assertNotNull(customerOrders);
            assertTrue(customerOrders.isEmpty());
        }
    }
    
    @Nested
    @DisplayName("Update Order Tests")
    class UpdateOrderTests {
        
        @BeforeEach
        void addTestOrders() {
            repository.addOrder(testOrder1);
            repository.addOrder(testOrder2);
        }
        
        @Test
        @DisplayName("Should update existing order")
        void shouldUpdateExistingOrder() {
            testOrder1.setStatus(RiceOrder.OrderStatus.DELIVERED);
            testOrder1.setPaymentMethod("E-Wallet");
            
            Optional<RiceOrder> result = repository.updateOrder("TEST001", testOrder1);
            
            assertTrue(result.isPresent());
            assertEquals(RiceOrder.OrderStatus.DELIVERED, result.get().getStatus());
            assertEquals("E-Wallet", result.get().getPaymentMethod());
        }
        
        @Test
        @DisplayName("Should preserve order ID when updating")
        void shouldPreserveOrderIdWhenUpdating() {
            testOrder1.setOrderId("DIFFERENT_ID");
            
            Optional<RiceOrder> result = repository.updateOrder("TEST001", testOrder1);
            
            assertTrue(result.isPresent());
            assertEquals("TEST001", result.get().getOrderId());
        }
        
        @Test
        @DisplayName("Should preserve order date when not provided in update")
        void shouldPreserveOrderDateWhenNotProvided() {
            Optional<RiceOrder> existingOrder = repository.getOrderById("TEST001");
            assertTrue(existingOrder.isPresent());
            LocalDateTime originalDate = existingOrder.get().getOrderDate();
            
            // Create a new order object for update without order date
            RiceOrder updateOrder = RiceOrder.builder()
                    .orderId("TEST001")
                    .customer(testOrder1.getCustomer())
                    .orderItems(testOrder1.getOrderItems())
                    .deliveryAddress(testOrder1.getDeliveryAddress())
                    .status(RiceOrder.OrderStatus.DELIVERED)
                    .orderDate(null) // Explicitly set to null
                    .deliveryTime(testOrder1.getDeliveryTime())
                    .paymentMethod("Updated Payment")
                    .totalAmount(testOrder1.getTotalAmount())
                    .build();
            
            Optional<RiceOrder> result = repository.updateOrder("TEST001", updateOrder);
            
            assertTrue(result.isPresent());
            assertEquals(originalDate, result.get().getOrderDate());
            assertEquals("Updated Payment", result.get().getPaymentMethod());
        }
        
        @Test
        @DisplayName("Should recalculate total amount when updating")
        void shouldRecalculateTotalAmountWhenUpdating() {
            testOrder1.setTotalAmount(null);
            
            Optional<RiceOrder> result = repository.updateOrder("TEST001", testOrder1);
            
            assertTrue(result.isPresent());
            assertNotNull(result.get().getTotalAmount());
        }
        
        @Test
        @DisplayName("Should return empty when updating non-existent order")
        void shouldReturnEmptyWhenUpdatingNonExistentOrder() {
            Optional<RiceOrder> result = repository.updateOrder("NONEXISTENT", testOrder1);
            
            assertTrue(result.isEmpty());
        }
        
        @Test
        @DisplayName("Should throw exception when update order is null")
        void shouldThrowExceptionWhenUpdateOrderIsNull() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                repository.updateOrder("TEST001", null);
            });
            
            assertEquals("Updated order cannot be null", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception when update order ID is null")
        void shouldThrowExceptionWhenUpdateOrderIdIsNull() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                repository.updateOrder(null, testOrder1);
            });
            
            assertEquals("Order ID cannot be null or empty", exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("Partial Update Order Tests")
    class PartialUpdateOrderTests {
        
        @BeforeEach
        void addTestOrders() {
            repository.addOrder(testOrder1);
        }
        
        @Test
        @DisplayName("Should partially update order status")
        void shouldPartiallyUpdateOrderStatus() {
            RiceOrder updates = RiceOrder.builder()
                    .status(RiceOrder.OrderStatus.CONFIRMED)
                    .build();
            
            Optional<RiceOrder> result = repository.partialUpdateOrder("TEST001", updates);
            
            assertTrue(result.isPresent());
            assertEquals(RiceOrder.OrderStatus.CONFIRMED, result.get().getStatus());
            assertEquals("John Doe", result.get().getCustomer().getName()); // Other fields unchanged
        }
        
        @Test
        @DisplayName("Should partially update customer")
        void shouldPartiallyUpdateCustomer() {
            Customer newCustomer = Customer.builder()
                    .customerId("CUST999")
                    .name("Updated Name")
                    .email("updated@test.com")
                    .phoneNumber("+1-555-9999")
                    .build();
            
            RiceOrder updates = RiceOrder.builder()
                    .customer(newCustomer)
                    .build();
            
            Optional<RiceOrder> result = repository.partialUpdateOrder("TEST001", updates);
            
            assertTrue(result.isPresent());
            assertEquals("Updated Name", result.get().getCustomer().getName());
            assertEquals(RiceOrder.OrderStatus.PENDING, result.get().getStatus()); // Status unchanged
        }
        
        @Test
        @DisplayName("Should partially update delivery address")
        void shouldPartiallyUpdateDeliveryAddress() {
            DeliveryAddress newAddress = DeliveryAddress.builder()
                    .street("999 New St")
                    .city("Boston")
                    .state("MA")
                    .postalCode("02101")
                    .country("USA")
                    .build();
            
            RiceOrder updates = RiceOrder.builder()
                    .deliveryAddress(newAddress)
                    .build();
            
            Optional<RiceOrder> result = repository.partialUpdateOrder("TEST001", updates);
            
            assertTrue(result.isPresent());
            assertEquals("Boston", result.get().getDeliveryAddress().getCity());
        }
        
        @Test
        @DisplayName("Should recalculate total when order items updated")
        void shouldRecalculateTotalWhenOrderItemsUpdated() {
            OrderItem newItem = OrderItem.builder()
                    .itemId("ITEM999")
                    .riceType("New Rice Type")
                    .quantity(5)
                    .pricePerUnit(new BigDecimal("20000"))
                    .spiceLevel("Mild")
                    .build();
            
            RiceOrder updates = RiceOrder.builder()
                    .orderItems(Arrays.asList(newItem))
                    .build();
            
            Optional<RiceOrder> result = repository.partialUpdateOrder("TEST001", updates);
            
            assertTrue(result.isPresent());
            assertEquals(new BigDecimal("100000"), result.get().getTotalAmount());
        }
        
        @Test
        @DisplayName("Should return empty when partially updating non-existent order")
        void shouldReturnEmptyWhenPartiallyUpdatingNonExistentOrder() {
            RiceOrder updates = RiceOrder.builder()
                    .status(RiceOrder.OrderStatus.CONFIRMED)
                    .build();
            
            Optional<RiceOrder> result = repository.partialUpdateOrder("NONEXISTENT", updates);
            
            assertTrue(result.isEmpty());
        }
        
        @Test
        @DisplayName("Should throw exception when partial updates is null")
        void shouldThrowExceptionWhenPartialUpdatesIsNull() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                repository.partialUpdateOrder("TEST001", null);
            });
            
            assertEquals("Updates cannot be null", exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("Delete Order Tests")
    class DeleteOrderTests {
        
        @BeforeEach
        void addTestOrders() {
            repository.addOrder(testOrder1);
            repository.addOrder(testOrder2);
            repository.addOrder(testOrder3);
        }
        
        @Test
        @DisplayName("Should remove order by ID")
        void shouldRemoveOrderById() {
            boolean result = repository.removeOrder("TEST001");
            
            assertTrue(result);
            assertEquals(2, repository.getOrderCount());
            assertFalse(repository.orderExists("TEST001"));
        }
        
        @Test
        @DisplayName("Should return false when removing non-existent order")
        void shouldReturnFalseWhenRemovingNonExistentOrder() {
            boolean result = repository.removeOrder("NONEXISTENT");
            
            assertFalse(result);
            assertEquals(3, repository.getOrderCount());
        }
        
        @Test
        @DisplayName("Should throw exception when removing order with null ID")
        void shouldThrowExceptionWhenRemovingOrderWithNullId() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                repository.removeOrder(null);
            });
            
            assertEquals("Order ID cannot be null or empty", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception when removing order with empty ID")
        void shouldThrowExceptionWhenRemovingOrderWithEmptyId() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                repository.removeOrder("");
            });
            
            assertEquals("Order ID cannot be null or empty", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should remove all orders")
        void shouldRemoveAllOrders() {
            repository.removeAllOrders();
            
            assertEquals(0, repository.getOrderCount());
            assertTrue(repository.getAllOrders().isEmpty());
        }
    }
    
    @Nested
    @DisplayName("Utility Method Tests")
    class UtilityMethodTests {
        
        @Test
        @DisplayName("Should return correct order count")
        void shouldReturnCorrectOrderCount() {
            assertEquals(0, repository.getOrderCount());
            
            repository.addOrder(testOrder1);
            assertEquals(1, repository.getOrderCount());
            
            repository.addOrder(testOrder2);
            assertEquals(2, repository.getOrderCount());
            
            repository.removeOrder("TEST001");
            assertEquals(1, repository.getOrderCount());
        }
        
        @Test
        @DisplayName("Should check if order exists")
        void shouldCheckIfOrderExists() {
            assertFalse(repository.orderExists("TEST001"));
            
            repository.addOrder(testOrder1);
            assertTrue(repository.orderExists("TEST001"));
            
            repository.removeOrder("TEST001");
            assertFalse(repository.orderExists("TEST001"));
        }
        
        @Test
        @DisplayName("Should return false for null order ID in exists check")
        void shouldReturnFalseForNullOrderIdInExistsCheck() {
            assertFalse(repository.orderExists(null));
        }
    }
    
    @Nested
    @DisplayName("Mock Data Initialization Tests")
    class MockDataInitializationTests {
        
        @Test
        @DisplayName("Should initialize with 5 mock orders")
        void shouldInitializeWithMockOrders() {
            RiceOrderRepository repoWithMockData = new RiceOrderRepository();
            repoWithMockData.initializeMockData();
            
            assertEquals(5, repoWithMockData.getOrderCount());
            
            // Verify specific mock orders exist
            assertTrue(repoWithMockData.orderExists("ORD001"));
            assertTrue(repoWithMockData.orderExists("ORD002"));
            assertTrue(repoWithMockData.orderExists("ORD003"));
            assertTrue(repoWithMockData.orderExists("ORD004"));
            assertTrue(repoWithMockData.orderExists("ORD005"));
        }
        
        @Test
        @DisplayName("Mock data should have different statuses")
        void mockDataShouldHaveDifferentStatuses() {
            RiceOrderRepository repoWithMockData = new RiceOrderRepository();
            repoWithMockData.initializeMockData();
            
            List<RiceOrder> allOrders = repoWithMockData.getAllOrders();
            
            // Check that there are orders with different statuses
            long uniqueStatuses = allOrders.stream()
                    .map(RiceOrder::getStatus)
                    .distinct()
                    .count();
            
            assertTrue(uniqueStatuses > 1, "Mock data should have multiple different statuses");
        }
        
        @Test
        @DisplayName("Mock data should have complete order details")
        void mockDataShouldHaveCompleteOrderDetails() {
            RiceOrderRepository repoWithMockData = new RiceOrderRepository();
            repoWithMockData.initializeMockData();
            
            Optional<RiceOrder> order = repoWithMockData.getOrderById("ORD001");
            assertTrue(order.isPresent());
            
            RiceOrder orderData = order.get();
            assertNotNull(orderData.getCustomer());
            assertNotNull(orderData.getOrderItems());
            assertFalse(orderData.getOrderItems().isEmpty());
            assertNotNull(orderData.getDeliveryAddress());
            assertNotNull(orderData.getStatus());
            assertNotNull(orderData.getOrderDate());
            assertNotNull(orderData.getTotalAmount());
        }
    }
}

