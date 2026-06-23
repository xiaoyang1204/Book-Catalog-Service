package com.example.bookcatalog.domain.mapper;

import com.example.bookcatalog.domain.dto.BookCreateRequest;
import com.example.bookcatalog.domain.dto.BookResponse;
import com.example.bookcatalog.domain.dto.BookUpdateRequest;
import com.example.bookcatalog.domain.entity.Book;

/**
 * BookMapper - 使用 Builder 模式实现对象转换
 * 设计模式: Builder Pattern
 * 将 Entity 与 DTO 之间的转换逻辑集中管理，避免在 Service 层散落转换代码
 */
public final class BookMapper {

    private BookMapper() {
        // 工具类禁止实例化
    }

    /**
     * 将创建请求 DTO 转换为 Entity
     */
    public static Book toEntity(BookCreateRequest request) {
        return Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .publishedDate(request.getPublishedDate())
                .description(request.getDescription())
                .available(true)
                .build();
    }

    /**
     * 将更新请求 DTO 合并到已有 Entity（仅更新非空字段）
     */
    public static Book mergeToEntity(BookUpdateRequest request, Book existingBook) {
        if (request.getTitle() != null) {
            existingBook.setTitle(request.getTitle());
        }
        if (request.getAuthor() != null) {
            existingBook.setAuthor(request.getAuthor());
        }
        if (request.getIsbn() != null) {
            existingBook.setIsbn(request.getIsbn());
        }
        if (request.getPublishedDate() != null) {
            existingBook.setPublishedDate(request.getPublishedDate());
        }
        if (request.getDescription() != null) {
            existingBook.setDescription(request.getDescription());
        }
        if (request.getAvailable() != null) {
            existingBook.setAvailable(request.getAvailable());
        }
        return existingBook;
    }

    /**
     * 将 Entity 转换为响应 DTO
     */
    public static BookResponse toResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .publishedDate(book.getPublishedDate())
                .description(book.getDescription())
                .available(book.isAvailable())
                .build();
    }
}
