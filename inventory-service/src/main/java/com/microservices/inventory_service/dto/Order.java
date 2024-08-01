package com.microservices.inventory_service.dto;

import jakarta.persistence.ElementCollection;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Long id;
    private Long userId;
    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;
    private Double totalAmount;
    @ElementCollection
    private List<Long> bookIds;
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;
}