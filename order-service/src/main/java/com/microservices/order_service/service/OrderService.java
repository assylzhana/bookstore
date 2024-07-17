package com.microservices.order_service.service;

import com.microservices.order_service.dto.InventoryItem;
import com.microservices.order_service.model.Order;
import com.microservices.order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;



    @Transactional
    public Order createOrder(Order order) {
        order.setStatus("PENDING");
        order.setPaymentStatus("not paid");
        Order savedOrder = orderRepository.save(order);

        return savedOrder;
    }


    @Transactional
    public Order updateOrder(Long orderId, Order updatedOrder) {
        return orderRepository.findById(orderId).map(existingOrder -> {
            existingOrder.setBookIds(updatedOrder.getBookIds());
            existingOrder.setTotalAmount(updatedOrder.getTotalAmount());
            existingOrder.setStatus(updatedOrder.getStatus());
            existingOrder.setPaymentStatus(updatedOrder.getPaymentStatus());
            Order savedOrder = orderRepository.save(existingOrder);
            return savedOrder;
        }).orElse(null);
    }

    @Transactional
    public boolean cancelOrder(Long orderId) {
        return orderRepository.findById(orderId).map(order -> {
            order.setStatus("CANCELLED");
            orderRepository.save(order);
            return true;
        }).orElse(false);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }


}
