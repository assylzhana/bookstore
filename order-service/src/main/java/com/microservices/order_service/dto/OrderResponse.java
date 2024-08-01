package com.microservices.order_service.dto;

import com.microservices.order_service.model.OrderStatus;
import com.microservices.order_service.model.PaymentStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private List<Long> bookIds;
    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;
}
