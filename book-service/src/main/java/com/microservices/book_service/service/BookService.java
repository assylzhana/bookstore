package com.microservices.book_service.service;

import com.microservices.book_service.dto.BookDto;
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
            BookDto bookEvent = new BookDto(
                    book.getId(), book.getTitle(), book.getDescription(), book.getAuthor(), book.getGenre(), book.getPageNumber(), "delete"
            );
            bookRepository.deleteById(id);
            sendBookEvent(bookEvent);
            sendBookEventToSearch(bookEvent);
        }
    }

    public void createBook(Book book) {
        bookRepository.save(book);
        BookDto bookEvent = new BookDto(
                book.getId(), book.getTitle(), book.getDescription(), book.getAuthor(), book.getGenre(), book.getPageNumber(), "create"
        );
        sendBookEvent(bookEvent);
        sendBookEventToSearch(bookEvent);
    }


    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBook(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book findBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public void editBook(Book book) {
        bookRepository.save(book);
        BookDto bookEvent = new BookDto(
                book.getId(), book.getTitle(), book.getDescription(), book.getAuthor(), book.getGenre(), book.getPageNumber(), "edit"
        );
        sendBookEventToSearch(bookEvent);
    }
}
