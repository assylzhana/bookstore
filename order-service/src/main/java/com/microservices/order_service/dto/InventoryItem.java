package com.microservices.order_service.dto;

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
    private String status;
    private Integer quantity;
    private Double price;
    private Long bookId;
}
