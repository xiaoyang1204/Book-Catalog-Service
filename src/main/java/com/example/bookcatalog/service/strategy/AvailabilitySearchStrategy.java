package com.example.bookcatalog.service.strategy;

import com.example.bookcatalog.domain.entity.Book;
import com.example.bookcatalog.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AvailabilitySearchStrategy implements BookSearchStrategy {

    private final BookRepository bookRepository;

    @Override
    public boolean supports(String searchType) {
        return "available".equalsIgnoreCase(searchType) || "unavailable".equalsIgnoreCase(searchType);
    }

    @Override
    public List<Book> search(String keyword) {
        boolean available = "available".equalsIgnoreCase(keyword);
        return bookRepository.findByAvailable(available);
    }
}
