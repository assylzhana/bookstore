package com.microservices.book_service.service;

import com.microservices.book_service.model.Book;
import com.microservices.book_service.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private static final String TOPIC = "book-events";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendBookCreatedEvent(Book book) {
        kafkaTemplate.send(TOPIC, book.getId().toString(), book);
    }

    private final BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBook(Long id) {
        return  bookRepository.findById(id).orElse(null);
    }
    public void createBook(Book book) {
        bookRepository.save(book);
    }

    public Book findBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public void editBook(Book book) {
        bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
