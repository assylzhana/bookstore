package com.microservices.inventory_service.dto;

import jakarta.persistence.ElementCollection;

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
    private String status;
    private Double totalAmount;
    @ElementCollection
    private List<Long> bookIds;
    private String paymentStatus;
}