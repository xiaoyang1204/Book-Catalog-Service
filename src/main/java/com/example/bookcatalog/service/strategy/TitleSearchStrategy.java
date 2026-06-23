package com.example.bookcatalog.service.strategy;

import com.example.bookcatalog.domain.entity.Book;
import com.example.bookcatalog.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TitleSearchStrategy implements BookSearchStrategy {

    private final BookRepository bookRepository;

    @Override
    public boolean supports(String searchType) {
        return "title".equalsIgnoreCase(searchType);
    }

    @Override
    public List<Book> search(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword);
    }
}
