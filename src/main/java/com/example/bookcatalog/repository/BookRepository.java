package com.example.bookcatalog.repository;

import com.example.bookcatalog.domain.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BookRepository {

    Optional<Book> findById(Long id);

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    List<Book> findAll();

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAvailable(boolean available);

    int save(Book book);

    int update(Book book);

    int deleteById(Long id);
}