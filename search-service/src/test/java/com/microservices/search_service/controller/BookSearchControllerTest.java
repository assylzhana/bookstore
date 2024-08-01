package com.microservices.search_service.controller;

import com.microservices.search_service.model.Book;
import com.microservices.search_service.repository.BookRepository;
import com.microservices.search_service.service.BookSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookSearchControllerTest {

    @Mock
    private BookSearchService bookSearchService;

    @InjectMocks
    private BookSearchController bookSearchController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void search(){
        String query = "Title";
        String sortBy = "title";
        Book book = new Book(1L, "Title", "Description", "Author", "Genre", 300);
        List<Book> books = Collections.singletonList(book);
        when(bookSearchService.searchBooks(query, sortBy)).thenReturn(books);
        List<Book> result = bookSearchController.searchBooks(query, sortBy);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Title", result.get(0).getTitle());
        verify(bookSearchService, times(1)).searchBooks(query, sortBy);
    }
}