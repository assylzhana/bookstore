package com.microservices.search_service.controller;

import com.microservices.search_service.model.Book;
import com.microservices.search_service.service.BookSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Slf4j
public class BookSearchController {

    private final BookSearchService searchService;

    @GetMapping()
    public List<Book> searchBooks(@RequestParam String query, @RequestParam(required = false) String sortBy) {
        log.info("Received query: {}", query);
        log.info("Received sortBy: {}", sortBy);
        return searchService.searchBooks(query, sortBy);
    }
}
