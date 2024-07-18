package com.microservices.search_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Document(indexName = "books")
public class Book {
    @Id
    private Long id;
    private String title;
    private String description;
    private String author;
    private String genre;
    private Integer pageNumber;

}
