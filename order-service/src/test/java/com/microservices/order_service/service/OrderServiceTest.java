package com.microservices.order_service.service;

import com.microservices.order_service.dto.InventoryItem;
import com.microservices.order_service.dto.UserDto;
import com.microservices.order_service.exception.OrderAlreadyPendingException;
import com.microservices.order_service.model.Order;
import com.microservices.order_service.model.OrderItemPrice;
import com.microservices.order_service.model.OrderStatus;
import com.microservices.order_service.model.PaymentStatus;
import com.microservices.order_service.repository.OrderItemPriceRepository;
import com.microservices.order_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemPriceRepository orderItemPriceRepository;

    @Mock
    private KafkaTemplate<String, Order> kafkaTemplate;

    @InjectMocks
    private OrderService orderService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void sendOrderCreatedEvent() {
        Order order = new Order();
        order.setId(1L);
        orderService.sendOrderCreatedEvent(order);
        verify(kafkaTemplate, times(1)).send("order-events", "1", order);
    }

    @Test
    void sendOrderCanceledEvent() {
        Order order = new Order();
        order.setId(1L);
        orderService.sendOrderCanceledEvent(order);
        verify(kafkaTemplate, times(1)).send("order-delete-events", "1", order);
    }

    @Test
    void sendOrderPayEvent() {
        Order order = new Order();
        order.setId(1L);
        orderService.sendOrderPayEvent(order);
        verify(kafkaTemplate, times(1)).send("order-pay-events", "1", order);
    }

    @Test
    void consumeBookEvent() {
        InventoryItem inventory = new InventoryItem();
        inventory.setBookId(1L);
        inventory.setPrice(100.0);
        OrderItemPrice orderItemPrice = new OrderItemPrice();
        orderItemPrice.setBookId(1L);
        orderItemPrice.setPrice(90.0);
        when(orderItemPriceRepository.findByBookId(1L)).thenReturn(List.of(orderItemPrice));
        orderService.consumeBookEvent(inventory);
        verify(orderItemPriceRepository, times(1)).save(any(OrderItemPrice.class));
    }

    @Test
    void createOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setBookIds(List.of(1L, 2L));
        OrderItemPrice orderItemPrice1 = new OrderItemPrice(1L, 1L, 100.0);
        OrderItemPrice orderItemPrice2 = new OrderItemPrice(2L, 2L, 200.0);
        when(orderItemPriceRepository.findByBookIdIn(List.of(1L, 2L))).thenReturn(List.of(orderItemPrice1, orderItemPrice2));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        Order createdOrder = orderService.createOrder(order);
        assertEquals(OrderStatus.PENDING, createdOrder.getStatus());
        assertEquals(PaymentStatus.not_paid, createdOrder.getPaymentStatus());
        assertEquals(300.0, createdOrder.getTotalAmount());
        verify(orderRepository, times(1)).save(order);
        verify(kafkaTemplate, times(1)).send("order-events", order.getId().toString(), order);
    }

    @Test
    void updateOrder() {
        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setBookIds(List.of(1L, 2L));
        Order updatedOrder = new Order();
        updatedOrder.setBookIds(List.of(3L, 4L));
        OrderItemPrice orderItemPrice1 = new OrderItemPrice(1L, 3L, 100.0);
        OrderItemPrice orderItemPrice2 = new OrderItemPrice(2L, 4L, 200.0);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderItemPriceRepository.findByBookIdIn(List.of(3L, 4L))).thenReturn(List.of(orderItemPrice1, orderItemPrice2));
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

        Order updated = orderService.updateOrder(1L, updatedOrder);

        assertEquals(300.0, updated.getTotalAmount());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(existingOrder);
    }

    @Test
    void cancelOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.not_paid);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        boolean isCanceled = orderService.cancelOrder(1L);
        assertTrue(isCanceled);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository, times(1)).save(order);
        verify(kafkaTemplate, times(1)).send("order-delete-events", order.getId().toString(), order);
    }

    @Test
    void getOrdersByUserId() {
        Long userId = 1L;
        List<Order> orders = List.of(new Order(), new Order());

        when(orderRepository.findAllByUserId(userId)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByUserId(userId);

        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    void getOrderById() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.getOrderById(orderId);

        assertTrue(result.isPresent());
        assertEquals(orderId, result.get().getId());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void deleteOrder() {
        Long orderId = 1L;

        orderService.deleteOrder(orderId);

        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    void pay() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        Order pendingOrder = new Order();
        pendingOrder.setId(1L);
        pendingOrder.setStatus(OrderStatus.PENDING);
        pendingOrder.setPaymentStatus(PaymentStatus.not_paid);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDto);
        when(orderRepository.findAllByUserIdAndStatus(1L, OrderStatus.PENDING)).thenReturn(List.of(pendingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(pendingOrder);
        Order result = orderService.pay();
        assertEquals(OrderStatus.SUCCESS, result.getStatus());
        assertEquals(PaymentStatus.paid, result.getPaymentStatus());
        verify(orderRepository, times(1)).save(pendingOrder);
        verify(kafkaTemplate, times(1)).send("order-pay-events", pendingOrder.getId().toString(), pendingOrder);
    }

    @Test
    void pay_shouldThrowException_whenMultiplePendingOrders() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        Order pendingOrder1 = new Order();
        pendingOrder1.setStatus(OrderStatus.PENDING);
        Order pendingOrder2 = new Order();
        pendingOrder2.setStatus(OrderStatus.PENDING);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDto);
        when(orderRepository.findAllByUserIdAndStatus(1L, OrderStatus.PENDING)).thenReturn(List.of(pendingOrder1, pendingOrder2));

        assertThrows(OrderAlreadyPendingException.class, () -> orderService.pay());

        verify(orderRepository, times(1)).findAllByUserIdAndStatus(1L, OrderStatus.PENDING);
        verifyNoMoreInteractions(orderRepository);
        verifyNoInteractions(kafkaTemplate);
    }
}
