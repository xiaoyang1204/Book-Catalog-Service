package com.example.bookcatalog.service;

import com.example.bookcatalog.domain.dto.BookCreateRequest;
import com.example.bookcatalog.domain.dto.BookResponse;
import com.example.bookcatalog.domain.dto.BookUpdateRequest;
import com.example.bookcatalog.domain.entity.Book;
import com.example.bookcatalog.domain.exception.BookNotFoundException;
import com.example.bookcatalog.domain.exception.DuplicateIsbnException;
import com.example.bookcatalog.domain.mapper.BookMapper;
import com.example.bookcatalog.repository.BookRepository;
import com.example.bookcatalog.service.strategy.BookSearchStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final List<BookSearchStrategy> searchStrategies;

    @Transactional
    public BookResponse createBook(BookCreateRequest request) {
        log.info("Creating book with ISBN: {}", request.getIsbn());

        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateIsbnException(request.getIsbn());
        }

        Book book = BookMapper.toEntity(request);
        bookRepository.save(book);

        log.info("Book created successfully with id: {}", book.getId());
        return BookMapper.toResponse(book);
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        log.info("Fetching book with id: {}", id);
        Book book = findBookOrThrow(id);
        return BookMapper.toResponse(book);
    }

    @Transactional(readOnly = true)
    public BookResponse getBookByIsbn(String isbn) {
        log.info("Fetching book with ISBN: {}", isbn);
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
        return BookMapper.toResponse(book);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        log.info("Fetching all books");
        return bookRepository.findAll().stream()
                .map(BookMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookResponse> searchBooks(String type, String keyword) {
        log.info("Searching books by type: {}, keyword: {}", type, keyword);
        return searchStrategies.stream()
                .filter(strategy -> strategy.supports(type))
                .findFirst()
                .map(strategy -> strategy.search(keyword))
                .orElseGet(() -> bookRepository.findAll())
                .stream()
                .map(BookMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookResponse updateBook(Long id, BookUpdateRequest request) {
        log.info("Updating book with id: {}", id);
        Book existingBook = findBookOrThrow(id);

        // 如果更新了 ISBN，检查新 ISBN 是否已被占用
        if (request.getIsbn() != null && !request.getIsbn().equals(existingBook.getIsbn())) {
            if (bookRepository.existsByIsbn(request.getIsbn())) {
                throw new DuplicateIsbnException(request.getIsbn());
            }
        }

        BookMapper.mergeToEntity(request, existingBook);
        bookRepository.update(existingBook);

        log.info("Book updated successfully with id: {}", existingBook.getId());
        return BookMapper.toResponse(existingBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        log.info("Deleting book with id: {}", id);
        Book book = findBookOrThrow(id);
        bookRepository.deleteById(book.getId());
        log.info("Book deleted successfully with id: {}", id);
    }

    private Book findBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }
}