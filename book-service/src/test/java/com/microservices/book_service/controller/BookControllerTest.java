package com.microservices.book_service.controller;

import com.microservices.book_service.model.Book;
import com.microservices.book_service.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBooks() {
        List<Book> books = Arrays.asList(
                new Book(1L, "Title1", "Description1","Author1", "Genre1", 100),
                new Book(2L, "Title2", "Description2", "Author2", "Genre2", 200)
        );
        when(bookService.getAllBooks()).thenReturn(books);
        ResponseEntity<List<Book>> response = bookController.getAllBooks();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void createBook() {
        Book book = new Book(null, "Title", "Description", "Author", "Genre", 100);
        Book createdBook = new Book(1L, "Title", "Description", "Author", "Genre", 100);
        when(bookService.createBook(book)).thenReturn(createdBook);
        ResponseEntity<?> response = bookController.createBook(book);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdBook, response.getBody());
        verify(bookService, times(1)).createBook(book);
    }

    @Test
    void getBook() {
        Long bookId = 1L;
        Book book = new Book(bookId, "Title", "Author", "Description", "Genre", 100);
        when(bookService.getBook(bookId)).thenReturn(book);
        ResponseEntity<?> response = bookController.getBook(bookId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(book, response.getBody());
        verify(bookService, times(1)).getBook(bookId);
    }

    @Test
    void editBook() {
        Long bookId = 1L;
        Book book = new Book(bookId, "Title", "Description", "Author","Genre", 100);
        Book newBook = new Book(bookId, "New Title",  "New Description","New Author", "New Genre", 150);
        when(bookService.findBookById(bookId)).thenReturn(book);
        doAnswer(invocation -> {
            Book b = invocation.getArgument(0);
            b.setTitle(newBook.getTitle());
            b.setAuthor(newBook.getAuthor());
            b.setDescription(newBook.getDescription());
            b.setGenre(newBook.getGenre());
            b.setPageNumber(newBook.getPageNumber());
            return null;
        }).when(bookService).editBook(book);
        ResponseEntity<?> response = bookController.editBook(bookId, newBook);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newBook.getTitle(), book.getTitle());
        assertEquals(newBook.getAuthor(), book.getAuthor());
        assertEquals(newBook.getDescription(), book.getDescription());
        assertEquals(newBook.getGenre(), book.getGenre());
        assertEquals(newBook.getPageNumber(), book.getPageNumber());
        verify(bookService, times(1)).findBookById(bookId);
        verify(bookService, times(1)).editBook(book);
    }

    @Test
    void deleteBook() {
        Long bookId = 1L;
        Book book = new Book(bookId, "Title", "Description", "Author", "Genre", 100);
        when(bookService.findBookById(bookId)).thenReturn(book);
        doNothing().when(bookService).deleteBook(bookId);
        ResponseEntity<Void> response = bookController.deleteBook(bookId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookService, times(1)).findBookById(bookId);
        verify(bookService, times(1)).deleteBook(bookId);
    }
}