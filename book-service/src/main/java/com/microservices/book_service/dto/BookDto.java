package com.microservices.book_service.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
