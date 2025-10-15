package com.example.riceapi.repository;

import com.example.riceapi.modal.Customer;
import com.example.riceapi.modal.DeliveryAddress;
import com.example.riceapi.modal.OrderItem;
import com.example.riceapi.modal.RiceOrder;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class RiceOrderRepository {
    
    // In-memory storage using ConcurrentHashMap for thread safety
    private final Map<String, RiceOrder> orders = new ConcurrentHashMap<>();
    
    /**
     * Initialize repository with mock data
     */
    @PostConstruct
    public void initializeMockData() {
        // Mock Order 1
        Customer customer1 = Customer.builder()
                .customerId("CUST001")
                .name("Ahmad Rizki")
                .email("ahmad.rizki@email.com")
                .phoneNumber("+62-812-3456-7890")
                .build();
        
        DeliveryAddress address1 = DeliveryAddress.builder()
                .street("Jl. Sudirman No. 123")
                .city("Jakarta")
                .state("DKI Jakarta")
                .postalCode("12190")
                .country("Indonesia")
                .additionalInstructions("Please call upon arrival")
                .build();
        
        OrderItem item1_1 = OrderItem.builder()
                .itemId("ITEM001")
                .riceType("Nasi Goreng Special")
                .quantity(2)
                .pricePerUnit(new BigDecimal("45000"))
                .spiceLevel("Medium")
                .additionalNotes("Extra shrimp please")
                .build();
        
        OrderItem item1_2 = OrderItem.builder()
                .itemId("ITEM002")
                .riceType("Nasi Goreng Ayam")
                .quantity(1)
                .pricePerUnit(new BigDecimal("35000"))
                .spiceLevel("Mild")
                .additionalNotes("No vegetables")
                .build();
        
        RiceOrder order1 = RiceOrder.builder()
                .orderId("ORD001")
                .customer(customer1)
                .orderItems(Arrays.asList(item1_1, item1_2))
                .deliveryAddress(address1)
                .status(RiceOrder.OrderStatus.DELIVERED)
                .orderDate(LocalDateTime.now().minusDays(2))
                .deliveryTime(LocalDateTime.now().minusDays(2).plusHours(1))
                .paymentMethod("Credit Card")
                .totalAmount(new BigDecimal("125000"))
                .build();
        
        // Mock Order 2
        Customer customer2 = Customer.builder()
                .customerId("CUST002")
                .name("Siti Nurhaliza")
                .email("siti.nur@email.com")
                .phoneNumber("+62-821-9876-5432")
                .build();
        
        DeliveryAddress address2 = DeliveryAddress.builder()
                .street("Jl. Gatot Subroto No. 456")
                .city("Bandung")
                .state("West Java")
                .postalCode("40123")
                .country("Indonesia")
                .additionalInstructions("Ring doorbell twice")
                .build();
        
        OrderItem item2_1 = OrderItem.builder()
                .itemId("ITEM003")
                .riceType("Nasi Goreng Seafood")
                .quantity(3)
                .pricePerUnit(new BigDecimal("55000"))
                .spiceLevel("Hot")
                .additionalNotes("Extra sambal")
                .build();
        
        RiceOrder order2 = RiceOrder.builder()
                .orderId("ORD002")
                .customer(customer2)
                .orderItems(Arrays.asList(item2_1))
                .deliveryAddress(address2)
                .status(RiceOrder.OrderStatus.OUT_FOR_DELIVERY)
                .orderDate(LocalDateTime.now().minusHours(3))
                .deliveryTime(LocalDateTime.now().plusMinutes(30))
                .paymentMethod("Cash")
                .totalAmount(new BigDecimal("165000"))
                .build();
        
        // Mock Order 3
        Customer customer3 = Customer.builder()
                .customerId("CUST003")
                .name("Budi Santoso")
                .email("budi.santoso@email.com")
                .phoneNumber("+62-813-5555-1234")
                .build();
        
        DeliveryAddress address3 = DeliveryAddress.builder()
                .street("Jl. Diponegoro No. 789")
                .city("Surabaya")
                .state("East Java")
                .postalCode("60241")
                .country("Indonesia")
                .additionalInstructions("Leave at security desk")
                .build();
        
        OrderItem item3_1 = OrderItem.builder()
                .itemId("ITEM004")
                .riceType("Nasi Goreng Kampung")
                .quantity(2)
                .pricePerUnit(new BigDecimal("30000"))
                .spiceLevel("Extra Hot")
                .additionalNotes("With fried egg on top")
                .build();
        
        OrderItem item3_2 = OrderItem.builder()
                .itemId("ITEM005")
                .riceType("Nasi Goreng Pete")
                .quantity(1)
                .pricePerUnit(new BigDecimal("40000"))
                .spiceLevel("Medium")
                .additionalNotes("Extra pete")
                .build();
        
        RiceOrder order3 = RiceOrder.builder()
                .orderId("ORD003")
                .customer(customer3)
                .orderItems(Arrays.asList(item3_1, item3_2))
                .deliveryAddress(address3)
                .status(RiceOrder.OrderStatus.PREPARING)
                .orderDate(LocalDateTime.now().minusMinutes(45))
                .deliveryTime(LocalDateTime.now().plusHours(1))
                .paymentMethod("E-Wallet")
                .totalAmount(new BigDecimal("100000"))
                .build();
        
        // Mock Order 4
        Customer customer4 = Customer.builder()
                .customerId("CUST004")
                .name("Dewi Lestari")
                .email("dewi.lestari@email.com")
                .phoneNumber("+62-822-7777-8888")
                .build();
        
        DeliveryAddress address4 = DeliveryAddress.builder()
                .street("Jl. Thamrin No. 321")
                .city("Yogyakarta")
                .state("Special Region of Yogyakarta")
                .postalCode("55511")
                .country("Indonesia")
                .additionalInstructions("Apartment unit 5B")
                .build();
        
        OrderItem item4_1 = OrderItem.builder()
                .itemId("ITEM006")
                .riceType("Nasi Goreng Special")
                .quantity(4)
                .pricePerUnit(new BigDecimal("45000"))
                .spiceLevel("Mild")
                .additionalNotes("Family size portion")
                .build();
        
        RiceOrder order4 = RiceOrder.builder()
                .orderId("ORD004")
                .customer(customer4)
                .orderItems(Arrays.asList(item4_1))
                .deliveryAddress(address4)
                .status(RiceOrder.OrderStatus.CONFIRMED)
                .orderDate(LocalDateTime.now().minusMinutes(20))
                .deliveryTime(LocalDateTime.now().plusMinutes(50))
                .paymentMethod("Debit Card")
                .totalAmount(new BigDecimal("180000"))
                .build();
        
        // Mock Order 5
        Customer customer5 = Customer.builder()
                .customerId("CUST005")
                .name("Eko Prasetyo")
                .email("eko.prasetyo@email.com")
                .phoneNumber("+62-856-4444-9999")
                .build();
        
        DeliveryAddress address5 = DeliveryAddress.builder()
                .street("Jl. Ahmad Yani No. 567")
                .city("Semarang")
                .state("Central Java")
                .postalCode("50149")
                .country("Indonesia")
                .additionalInstructions("Office building, 3rd floor")
                .build();
        
        OrderItem item5_1 = OrderItem.builder()
                .itemId("ITEM007")
                .riceType("Nasi Goreng Ayam")
                .quantity(1)
                .pricePerUnit(new BigDecimal("35000"))
                .spiceLevel("Hot")
                .additionalNotes("Extra crispy")
                .build();
        
        OrderItem item5_2 = OrderItem.builder()
                .itemId("ITEM008")
                .riceType("Nasi Goreng Seafood")
                .quantity(1)
                .pricePerUnit(new BigDecimal("55000"))
                .spiceLevel("Medium")
                .additionalNotes("No squid")
                .build();
        
        OrderItem item5_3 = OrderItem.builder()
                .itemId("ITEM009")
                .riceType("Nasi Goreng Kampung")
                .quantity(2)
                .pricePerUnit(new BigDecimal("30000"))
                .spiceLevel("Mild")
                .additionalNotes("Regular portion")
                .build();
        
        RiceOrder order5 = RiceOrder.builder()
                .orderId("ORD005")
                .customer(customer5)
                .orderItems(Arrays.asList(item5_1, item5_2, item5_3))
                .deliveryAddress(address5)
                .status(RiceOrder.OrderStatus.PENDING)
                .orderDate(LocalDateTime.now().minusMinutes(5))
                .deliveryTime(LocalDateTime.now().plusHours(2))
                .paymentMethod("Cash")
                .totalAmount(new BigDecimal("150000"))
                .build();
        
        // Add all orders to the repository
        orders.put(order1.getOrderId(), order1);
        orders.put(order2.getOrderId(), order2);
        orders.put(order3.getOrderId(), order3);
        orders.put(order4.getOrderId(), order4);
        orders.put(order5.getOrderId(), order5);
    }
    
    /**
     * Retrieve list of all rice orders
     * 
     * @return List of all rice orders
     */
    public List<RiceOrder> getAllOrders() {
        return new ArrayList<>(orders.values());
    }
    
    /**
     * Retrieve list of rice orders filtered by status
     * 
     * @param status The order status to filter by
     * @return List of rice orders with the specified status
     */
    public List<RiceOrder> getOrdersByStatus(RiceOrder.OrderStatus status) {
        return orders.values().stream()
                .filter(order -> order.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieve a single rice order by ID
     * 
     * @param orderId The order ID to search for
     * @return Optional containing the order if found, empty otherwise
     */
    public Optional<RiceOrder> getOrderById(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }
    
    /**
     * Retrieve rice orders by customer ID
     * 
     * @param customerId The customer ID to search for
     * @return List of rice orders for the specified customer
     */
    public List<RiceOrder> getOrdersByCustomerId(String customerId) {
        return orders.values().stream()
                .filter(order -> order.getCustomer() != null && 
                                customerId.equals(order.getCustomer().getCustomerId()))
                .collect(Collectors.toList());
    }
    
    /**
     * Add a new rice order
     * 
     * @param order The rice order to add
     * @return The added rice order
     * @throws IllegalArgumentException if order is null or orderId is null/empty
     */
    public RiceOrder addOrder(RiceOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (order.getOrderId() == null || order.getOrderId().trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }
        if (orders.containsKey(order.getOrderId())) {
            throw new IllegalArgumentException("Order with ID " + order.getOrderId() + " already exists");
        }
        
        // Set order date if not already set
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }
        
        // Calculate and set total amount if not already set
        if (order.getTotalAmount() == null) {
            order.setTotalAmount(order.calculateTotalAmount());
        }
        
        orders.put(order.getOrderId(), order);
        return order;
    }
    
    /**
     * Update an existing rice order
     * 
     * @param orderId The ID of the order to update
     * @param updatedOrder The updated order data
     * @return Optional containing the updated order if found, empty otherwise
     * @throws IllegalArgumentException if updatedOrder is null
     */
    public Optional<RiceOrder> updateOrder(String orderId, RiceOrder updatedOrder) {
        if (updatedOrder == null) {
            throw new IllegalArgumentException("Updated order cannot be null");
        }
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }
        
        if (!orders.containsKey(orderId)) {
            return Optional.empty();
        }
        
        // Preserve the orderId and orderDate from the original order
        RiceOrder existingOrder = orders.get(orderId);
        updatedOrder.setOrderId(orderId);
        if (updatedOrder.getOrderDate() == null) {
            updatedOrder.setOrderDate(existingOrder.getOrderDate());
        }
        
        // Recalculate total amount
        if (updatedOrder.getTotalAmount() == null) {
            updatedOrder.setTotalAmount(updatedOrder.calculateTotalAmount());
        }
        
        orders.put(orderId, updatedOrder);
        return Optional.of(updatedOrder);
    }
    
    /**
     * Partially update an existing rice order (only updates non-null fields)
     * 
     * @param orderId The ID of the order to update
     * @param updates The partial updates to apply
     * @return Optional containing the updated order if found, empty otherwise
     */
    public Optional<RiceOrder> partialUpdateOrder(String orderId, RiceOrder updates) {
        if (updates == null) {
            throw new IllegalArgumentException("Updates cannot be null");
        }
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }
        
        RiceOrder existingOrder = orders.get(orderId);
        if (existingOrder == null) {
            return Optional.empty();
        }
        
        // Apply partial updates
        if (updates.getCustomer() != null) {
            existingOrder.setCustomer(updates.getCustomer());
        }
        if (updates.getOrderItems() != null) {
            existingOrder.setOrderItems(updates.getOrderItems());
        }
        if (updates.getDeliveryAddress() != null) {
            existingOrder.setDeliveryAddress(updates.getDeliveryAddress());
        }
        if (updates.getStatus() != null) {
            existingOrder.setStatus(updates.getStatus());
        }
        if (updates.getDeliveryTime() != null) {
            existingOrder.setDeliveryTime(updates.getDeliveryTime());
        }
        if (updates.getPaymentMethod() != null) {
            existingOrder.setPaymentMethod(updates.getPaymentMethod());
        }
        
        // Recalculate total amount if order items were updated
        if (updates.getOrderItems() != null) {
            existingOrder.setTotalAmount(existingOrder.calculateTotalAmount());
        } else if (updates.getTotalAmount() != null) {
            existingOrder.setTotalAmount(updates.getTotalAmount());
        }
        
        return Optional.of(existingOrder);
    }
    
    /**
     * Remove a rice order by ID
     * 
     * @param orderId The ID of the order to remove
     * @return true if the order was removed, false if it didn't exist
     */
    public boolean removeOrder(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }
        return orders.remove(orderId) != null;
    }
    
    /**
     * Remove all rice orders
     */
    public void removeAllOrders() {
        orders.clear();
    }
    
    /**
     * Get the total count of orders
     * 
     * @return The number of orders in the repository
     */
    public int getOrderCount() {
        return orders.size();
    }
    
    /**
     * Check if an order exists
     * 
     * @param orderId The order ID to check
     * @return true if the order exists, false otherwise
     */
    public boolean orderExists(String orderId) {
        if (orderId == null) {
            return false;
        }
        return orders.containsKey(orderId);
    }
}

