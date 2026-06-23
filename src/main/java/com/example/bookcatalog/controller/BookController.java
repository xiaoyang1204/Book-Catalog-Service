package com.example.bookcatalog.controller;

import com.example.bookcatalog.domain.dto.BookCreateRequest;
import com.example.bookcatalog.domain.dto.BookResponse;
import com.example.bookcatalog.domain.dto.BookUpdateRequest;
import com.example.bookcatalog.service.BookService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * 创建新图书
     *
     * @param request 图书创建请求（包含标题、作者、ISBN、出版年份、可用状态）
     * @return 201 Created - 创建成功的图书信息
     * @throws com.example.bookcatalog.domain.exception.DuplicateIsbnException 如果 ISBN 已存在
     */
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookCreateRequest request) {
        BookResponse response = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 根据 ID 获取图书详情
     *
     * @param id 图书 ID
     * @return 200 OK - 图书详细信息
     * @throws com.example.bookcatalog.domain.exception.BookNotFoundException 如果图书不存在
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        BookResponse response = bookService.getBookById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据 ISBN 获取图书详情git rm -r --cached target/
git rm -r --cached target/

     *
     * @param isbn 国际标准书号
     * @return 200 OK - 图书详细信息
     * @throws com.example.bookcatalog.domain.exception.BookNotFoundException 如果图书不存在
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookResponse> getBookByIsbn(@PathVariable String isbn) {
        BookResponse response = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有图书列表
     *
     * @return 200 OK - 图书列表（可能为空）
     */
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<BookResponse> responses = bookService.getAllBooks();
        return ResponseEntity.ok(responses);
    }

    /**
     * 按指定条件搜索图书
     * <p>
     * 支持的搜索类型：
     * <ul>
     *   <li>{@code author} - 按作者搜索</li>
     *   <li>{@code title} - 按标题搜索</li>
     *   <li>{@code available} - 按可用状态搜索</li>
     * </ul>
     *
     * @param type    搜索类型（author / title / available）
     * @param keyword 搜索关键词
     * @return 200 OK - 匹配的图书列表（可能为空）
     */
    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> searchBooks(
            @RequestParam String type,
            @RequestParam String keyword) {
        List<BookResponse> responses = bookService.searchBooks(type, keyword);
        return ResponseEntity.ok(responses);
    }

    /**
     * 更新指定图书的信息
     *
     * @param id      图书 ID
     * @param request 图书更新请求
     * @return 200 OK - 更新后的图书信息
     * @throws com.example.bookcatalog.domain.exception.BookNotFoundException 如果图书不存在
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequest request) {
        BookResponse response = bookService.updateBook(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除指定图书
     *
     * @param id 图书 ID
     * @return 204 No Content - 删除成功
     * @throws com.example.bookcatalog.domain.exception.BookNotFoundException 如果图书不存在
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
