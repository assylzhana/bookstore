package com.microservices.book_service.controller;

import com.microservices.book_service.model.Book;
import com.microservices.book_service.service.BookService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/all")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createBook(@RequestBody Book book){
        bookService.createBook(book);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        Book book = bookService.getBook(id);
        return ResponseEntity.ok(book);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Book> editBook(@PathVariable Long id , @RequestBody Book newbook){
        Book book = bookService.findBookById(id);
        if(book != null){
            book.setTitle(newbook.getTitle());
            book.setDescription(newbook.getDescription());
            book.setAuthor(newbook.getAuthor());
            book.setPageNumber(newbook.getPageNumber());
            bookService.editBook(book);
            return ResponseEntity.ok(book);
        }
        return ResponseEntity.notFound().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        Book book = bookService.findBookById(id);
        if(book != null){
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
