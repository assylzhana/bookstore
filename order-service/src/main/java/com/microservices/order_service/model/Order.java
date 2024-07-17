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
    private String status;
    private Double totalAmount;
    @ElementCollection
    private List<Long> bookIds;
    private String paymentStatus;

    @ElementCollection
    @MapKeyColumn(name = "book_id")
    @Column(name = "price")
    @CollectionTable(name = "order_book_prices", joinColumns = @JoinColumn(name = "order_id"))
    private Map<Long, Double> bookPrices = new HashMap<>();
}
