package com.microservices.order_service.controller;

import com.microservices.order_service.dto.OrderRequest;
import com.microservices.order_service.model.Order;
import com.microservices.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        Order order = new Order();
        order.setUserId(orderRequest.getUserId());
        order.setBookIds(orderRequest.getBookIds());
        Order savedOrder = orderService.createOrder(order);
        return ResponseEntity.ok(savedOrder);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long orderId, @RequestBody Order updatedOrder) {
        Order id = orderService.getOrderById(orderId).orElseThrow();
        if (id.getStatus().equals("CANCELLED") || id.getPaymentStatus().equals("paid")){
            return ResponseEntity.ok(id);
        }
        else{
            if (id.getPaymentStatus().equals("not paid")){
               updatedOrder.setPaymentStatus("not paid");
            }
            if (id.getStatus().equals("PENDING")){
                updatedOrder.setStatus("PENDING");
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
