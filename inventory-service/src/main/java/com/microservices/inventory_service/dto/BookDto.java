package com.microservices.inventory_service.dto;

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
public class BookDto {
    private Long id;
    private String title;
    private String description;
    private String author;
    private String genre;
    private Integer pageNumber;
    @Enumerated(value = EnumType.STRING)
    private Action action;
}
