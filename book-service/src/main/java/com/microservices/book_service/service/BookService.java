package com.microservices.book_service.service;

import com.microservices.book_service.dto.Action;
import com.microservices.book_service.dto.BookDto;
import com.microservices.book_service.model.Book;
import com.microservices.book_service.repository.BookRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private static final String TOPIC = "book-events";
    private static final String SEARCH_TOPIC = "search-events";
    private final KafkaTemplate<String, BookDto> kafkaTemplate;
    private final BookRepository bookRepository;

    public void sendBookEvent(BookDto book) {
        kafkaTemplate.send(TOPIC, book.getId().toString(), book);
    }

    public void sendBookEventToSearch(BookDto book) {
        kafkaTemplate.send(SEARCH_TOPIC, book.getId().toString(), book);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null) {
            BookDto bookEvent = BookDto.builder()
                    .id(book.getId())
                    .title(book.getTitle())
                    .description(book.getDescription())
                    .author(book.getAuthor())
                    .genre(book.getGenre())
                    .pageNumber(book.getPageNumber())
                    .action(Action.delete)
                    .build();
            bookRepository.deleteById(id);
            sendBookEvent(bookEvent);
            sendBookEventToSearch(bookEvent);
        }
    }

    public Book createBook(Book book) {
        Book savedBook = bookRepository.save(book);
        BookDto bookEvent = BookDto.builder()
                .id(savedBook.getId())
                .title(savedBook.getTitle())
                .description(savedBook.getDescription())
                .author(savedBook.getAuthor())
                .genre(savedBook.getGenre())
                .pageNumber(savedBook.getPageNumber())
                .action(Action.create)
                .build();
        sendBookEvent(bookEvent);
        sendBookEventToSearch(bookEvent);
        return savedBook;
    }


    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBook(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book with id " + id + " is not found"));
    }

    public Book findBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book with id " + id + " is not found"));
    }

    public void editBook(Book bookForEdit) {
        Book book = bookRepository.save(bookForEdit);
        BookDto bookEvent = BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .description(book.getDescription())
                .author(book.getAuthor())
                .genre(book.getGenre())
                .pageNumber(book.getPageNumber())
                .action(Action.edit)
                .build();
        sendBookEventToSearch(bookEvent);
    }
}
