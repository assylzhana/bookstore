package com.microservices.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private Long userId;
    private OrderStatus status;
    private Double totalAmount;
    private List<Long> bookIds;
    private PaymentStatus paymentStatus;
}
