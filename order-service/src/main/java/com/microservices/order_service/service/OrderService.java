package com.microservices.order_service.service;

import com.microservices.order_service.dto.CustomUserDetails;
import com.microservices.order_service.dto.InventoryItem;
import com.microservices.order_service.model.Order;
import com.microservices.order_service.model.OrderItemPrice;
import com.microservices.order_service.repository.OrderItemPriceRepository;
import com.microservices.order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemPriceRepository orderItemPriceRepository;

    @KafkaListener(topics = "inventory-events", groupId = "inventory_order_id")
    public void consumeBookEvent(InventoryItem inventory) {
        System.out.println("Received event for inventory: " + inventory);
        List<OrderItemPrice> prices = orderItemPriceRepository.findByBookId(inventory.getBookId());
        if (!prices.isEmpty()) {
            OrderItemPrice price = prices.get(0);
            price.setPrice(inventory.getPrice());
            orderItemPriceRepository.save(price);
        } else {
            OrderItemPrice newPrice = new OrderItemPrice();
            newPrice.setBookId(inventory.getBookId());
            newPrice.setPrice(inventory.getPrice());
            orderItemPriceRepository.save(newPrice);
        }
    }


    @Transactional
    public Order createOrder(Order order) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        order.setStatus("PENDING");
        order.setPaymentStatus("not paid");
        order.setTotalAmount(calculateTotalAmount(order.getBookIds()));
        Order savedOrder = orderRepository.save(order);
        return savedOrder;
    }

    private Double calculateTotalAmount(List<Long> bookIds) {
        double sum = 0.0;
        List<OrderItemPrice> orderItemPrices = orderItemPriceRepository.findByBookIdIn(bookIds);
        for (OrderItemPrice orderItemPrice : orderItemPrices) {
            sum += orderItemPrice.getPrice();
        }
        return sum;
    }


    @Transactional
    public Order updateOrder(Long orderId, Order updatedOrder) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order existingOrder = optionalOrder.get();
            existingOrder.setBookIds(updatedOrder.getBookIds());
            existingOrder.setTotalAmount(calculateTotalAmount(updatedOrder.getBookIds()));
            existingOrder.setStatus(updatedOrder.getStatus());
            existingOrder.setPaymentStatus("not paid");
            Order savedOrder = orderRepository.save(existingOrder);
            return savedOrder;
        } else {
            return null;
        }
    }
    @Transactional
    public boolean cancelOrder(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus("CANCELLED");
            orderRepository.save(order);
            return true;
        } else {
            return false;
        }
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }


    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
}
