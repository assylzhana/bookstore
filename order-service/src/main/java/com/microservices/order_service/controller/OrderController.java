package com.microservices.order_service.controller;

import com.microservices.order_service.dto.OrderRequest;
import com.microservices.order_service.dto.UserDto;
import com.microservices.order_service.model.Order;
import com.microservices.order_service.model.OrderStatus;
import com.microservices.order_service.model.PaymentStatus;
import com.microservices.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/pay")
    public ResponseEntity<String> pay() {
        Order orderForPay = orderService.pay();
        return ResponseEntity.ok("Send for pay : "+orderForPay.getId());
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        Order order = new Order();
        order.setBookIds(orderRequest.getBookIds());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDto userDto) {
            order.setUserId(userDto.getId());
            Order savedOrder = orderService.createOrder(order);
            return ResponseEntity.ok(savedOrder);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long orderId, @RequestBody Order updatedOrder) {
        Order id = orderService.getOrderById(orderId).orElseThrow();
        if (id.getStatus().equals(OrderStatus.CANCELLED) || id.getPaymentStatus().equals(PaymentStatus.paid)){
            return ResponseEntity.ok(id);
        }
        else{
            if (id.getPaymentStatus().equals(PaymentStatus.not_paid)){
               updatedOrder.setPaymentStatus(PaymentStatus.not_paid);
            }if (id.getStatus().equals(OrderStatus.PENDING)){
                updatedOrder.setStatus(OrderStatus.PENDING);
            }
        }
        Order order = orderService.updateOrder(orderId, updatedOrder);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        boolean isCanceled = orderService.cancelOrder(orderId);
        if (isCanceled) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("delete/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId).orElse(null);
        if (order != null) {
            orderService.deleteOrder(orderId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Optional<Order>> getOrderById(@PathVariable Long orderId) {
        Optional<Order> order = orderService.getOrderById(orderId);
        if (order.isPresent()) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }
}
