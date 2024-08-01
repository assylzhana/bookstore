package com.microservices.order_service.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItem {

    private Long id;
    @Enumerated(value = EnumType.STRING)
    private Status status;
    private Integer quantity;
    private Double price;
    private Long bookId;
}
