package com.microservices.order_service.repository;

import com.microservices.order_service.model.Order;
import com.microservices.order_service.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository  extends JpaRepository<Order, Long> {
    List<Order> findAllByUserId(Long userId);

    Order findByUserId(Long userId);

    List<Order> findAllByUserIdAndStatus(Long userId, OrderStatus orderStatus);
}
