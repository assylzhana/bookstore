package com.microservices.order_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
