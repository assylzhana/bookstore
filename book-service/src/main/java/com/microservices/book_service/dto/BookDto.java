package com.microservices.book_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
    private Long id;
    private String title;
    private String description;
    private String author;
    private String genre;
    private Integer pageNumber;
    private String action;
}
