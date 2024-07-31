package com.microservices.search_service.repository;

import com.microservices.search_service.model.Book;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends ElasticsearchRepository<Book, Long> {
    List<Book> findByTitleContainingOrDescriptionContainingOrAuthorContainingOrGenreContaining(String title, String description, String author, String genre);
    List<Book> findByTitleContainingOrDescriptionContainingOrAuthorContainingOrGenreContaining(String title, String description, String author, String genre, Sort sort);
}
