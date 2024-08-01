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
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemPriceRepository orderItemPriceRepository;

    private static final String TOPIC = "order-events";

    private static final String TOPIC1 = "order-delete-events";

    private static final String TOPIC2 = "order-pay-events";

    private final KafkaTemplate<String, Order> kafkaTemplate;

    public void sendOrderCreatedEvent(Order order) {
        kafkaTemplate.send(TOPIC, order.getId().toString(), order);
    }

    public void sendOrderCanceledEvent(Order order) {
        kafkaTemplate.send(TOPIC1, order.getId().toString(), order);
    }

    public void sendOrderPayEvent(Order order) {
        kafkaTemplate.send(TOPIC2, order.getId().toString(), order);
    }

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
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.not_paid);
        order.setTotalAmount(calculateTotalAmount(order.getBookIds()));
        Order savedOrder = orderRepository.save(order);
        sendOrderCreatedEvent(savedOrder);
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
            existingOrder.setPaymentStatus(PaymentStatus.not_paid);
            return orderRepository.save(existingOrder);
        } else {
            return null;
        }
    }

    @Transactional
    public boolean cancelOrder(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(OrderStatus.CANCELLED);
            if (!order.getPaymentStatus().equals(PaymentStatus.paid)) {
                sendOrderCanceledEvent(order);
            }
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

    @Transactional
    public Order pay() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Long userId = null;
        if (principal instanceof UserDto userDto) {
            userId = userDto.getId();
        }
        List<Order> pendingOrders = orderRepository.findAllByUserIdAndStatus(userId, OrderStatus.PENDING);
        if (pendingOrders.size() != 1) {
            throw new OrderAlreadyPendingException("U have pending order");
        }
        Order pendingOrder = pendingOrders.get(0);
        pendingOrder.setStatus(OrderStatus.SUCCESS);
        pendingOrder.setPaymentStatus(PaymentStatus.paid);
        orderRepository.save(pendingOrder);
        sendOrderPayEvent(pendingOrder);
        return pendingOrder;
    }
}
