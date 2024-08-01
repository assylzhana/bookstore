package com.microservices.order_service.controller;

import com.microservices.order_service.dto.OrderRequest;
import com.microservices.order_service.dto.UserDto;
import com.microservices.order_service.model.Order;
import com.microservices.order_service.model.OrderStatus;
import com.microservices.order_service.model.PaymentStatus;
import com.microservices.order_service.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void pay() {
        Order order = new Order();
        order.setId(1L);
        when(orderService.pay()).thenReturn(order);
        ResponseEntity<String> response = orderController.pay();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Send for pay : 1", response.getBody());
        verify(orderService, times(1)).pay();
    }

    @Test
    void createOrder() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setBookIds(Arrays.asList(1L, 2L));
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        Order order = new Order();
        order.setUserId(userDto.getId());
        order.setBookIds(orderRequest.getBookIds());
        when(authentication.getPrincipal()).thenReturn(userDto);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(orderService.createOrder(any(Order.class))).thenReturn(order);
        ResponseEntity<Order> response = orderController.createOrder(orderRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(order, response.getBody());
        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    @Test
    void updateOrder() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.not_paid);
        Order updatedOrder = new Order();
        updatedOrder.setStatus(OrderStatus.CANCELLED);
        updatedOrder.setPaymentStatus(PaymentStatus.paid);
        when(orderService.getOrderById(orderId)).thenReturn(Optional.of(order));
        when(orderService.updateOrder(any(Long.class), any(Order.class))).thenReturn(updatedOrder);
        ResponseEntity<Order> response = orderController.updateOrder(orderId, updatedOrder);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedOrder, response.getBody());
        verify(orderService, times(1)).getOrderById(orderId);
        verify(orderService, times(1)).updateOrder(any(Long.class), any(Order.class));
    }

    @Test
    void cancelOrder() {
        Long orderId = 1L;
        when(orderService.cancelOrder(orderId)).thenReturn(true);
        ResponseEntity<Void> response = orderController.cancelOrder(orderId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).cancelOrder(orderId);
    }

    @Test
    void deleteOrder() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        when(orderService.getOrderById(orderId)).thenReturn(Optional.of(order));
        doNothing().when(orderService).deleteOrder(orderId);
        ResponseEntity<Void> response = orderController.deleteOrder(orderId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).getOrderById(orderId);
        verify(orderService, times(1)).deleteOrder(orderId);
    }

    @Test
    void getOrdersByUserId() {
        Long userId = 1L;
        List<Order> orders = Arrays.asList(new Order(), new Order());
        when(orderService.getOrdersByUserId(userId)).thenReturn(orders);
        ResponseEntity<List<Order>> response = orderController.getOrdersByUserId(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orders, response.getBody());
        verify(orderService, times(1)).getOrdersByUserId(userId);
    }

    @Test
    void getOrderById() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        when(orderService.getOrderById(orderId)).thenReturn(Optional.of(order));
        ResponseEntity<Optional<Order>> response = orderController.getOrderById(orderId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Optional.of(order), response.getBody());
        verify(orderService, times(1)).getOrderById(orderId);
    }
}
