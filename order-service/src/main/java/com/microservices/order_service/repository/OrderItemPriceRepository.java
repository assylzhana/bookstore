package com.microservices.order_service.repository;

import com.microservices.order_service.model.OrderItemPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemPriceRepository extends JpaRepository<OrderItemPrice, Long> {
    List<OrderItemPrice> findByBookId(Long bookId);

    List<OrderItemPrice> findByBookIdIn(List<Long> bookIds);
}
