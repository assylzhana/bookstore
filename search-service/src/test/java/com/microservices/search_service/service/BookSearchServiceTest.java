package com.microservices.search_service.service;

import com.microservices.search_service.model.Book;
import com.microservices.search_service.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class BookSearchServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookSearchService bookSearchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void searchBooks() {
        String query = "Title";
        String sortBy = "title";
        Book book = new Book(1L, "Title", "Description", "Author", "Genre", 300);

        when(bookRepository.findByTitleContainingOrDescriptionContainingOrAuthorContainingOrGenreContaining(
                anyString(), anyString(), anyString(), anyString(), any(Sort.class)))
                .thenReturn(Collections.singletonList(book));

        List<Book> result = bookSearchService.searchBooks(query, sortBy);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Title", result.get(0).getTitle());
        verify(bookRepository, times(1)).findByTitleContainingOrDescriptionContainingOrAuthorContainingOrGenreContaining(
                query, query, query, query, Sort.by(Sort.Order.asc(sortBy + ".keyword"))
        );
    }
}