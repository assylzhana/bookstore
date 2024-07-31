package com.microservices.search_service.service;

import com.microservices.search_service.dto.Action;
import com.microservices.search_service.dto.BookDto;
import com.microservices.search_service.model.Book;
import com.microservices.search_service.repository.BookRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookSearchService {

    private final BookRepository bookRepository;

    @KafkaListener(topics = "search-events", groupId = "search_id")
    public void consumeBookEvent(BookDto bookDto) {
        log.info("Received book event for {}", bookDto.getTitle());
        if (bookDto.getAction().equals(Action.create)) {
            Book book = new Book(
                    bookDto.getId(),
                    bookDto.getTitle(),
                    bookDto.getDescription(),
                    bookDto.getAuthor(),
                    bookDto.getGenre(),
                    bookDto.getPageNumber()
            );
            bookRepository.save(book);
        }
        else if (bookDto.getAction().equals(Action.delete)) {
            bookRepository.deleteById(bookDto.getId());
        }
        else if (bookDto.getAction().equals(Action.edit)) {
            if (bookRepository.findById(bookDto.getId()).isPresent()){
                Book book = bookRepository.findById(bookDto.getId()).orElseThrow(()-> new NotFoundException("Book is not found"));
                book.setDescription(book.getDescription());
                book.setGenre(book.getGenre());
                book.setTitle(book.getTitle());
                book.setAuthor(book.getAuthor());
                book.setPageNumber(book.getPageNumber());
                bookRepository.save(book);
            }
        }
    }

    public List<Book> searchBooks(String query, String sortBy) {
        try {
            if (sortBy != null && !sortBy.isEmpty()) {
                log.info("Sort by: {}", sortBy);
                Sort sort = Sort.by(Sort.Order.asc(sortBy + ".keyword"));
                return bookRepository.findByTitleContainingOrDescriptionContainingOrAuthorContainingOrGenreContaining(query, query, query, query, sort);
            } else {
                return bookRepository.findByTitleContainingOrDescriptionContainingOrAuthorContainingOrGenreContaining(query, query, query, query);
            }
        } catch (Exception e) {
            log.error("Error during search: ", e);
            throw new RuntimeException("Search operation failed", e);
        }
    }
}
