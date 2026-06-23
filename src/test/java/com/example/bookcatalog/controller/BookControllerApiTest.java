package com.example.bookcatalog.controller;

import com.example.bookcatalog.domain.dto.BookCreateRequest;
import com.example.bookcatalog.domain.dto.BookUpdateRequest;
import com.example.bookcatalog.domain.entity.Book;
import com.example.bookcatalog.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("BookController API 集成测试")
class BookControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    private Book existingBook;

    @BeforeEach
    void setUp() {
        bookRepository.findAll().forEach(b -> bookRepository.deleteById(b.getId()));
        Book book = Book.builder()
                .title("Effective Java")
                .author("Joshua Bloch")
                .isbn("978-0134685991")
                .publishedDate(LocalDate.of(2018, 1, 6))
                .description("A must-read for Java developers")
                .available(true)
                .build();
        bookRepository.save(book);
        existingBook = book;
    }

    @Nested
    @DisplayName("POST /api/v1/books - 创建书籍")
    class CreateBookApiTests {

        @Test
        @DisplayName("应成功创建书籍并返回201")
        void shouldCreateBookAndReturn201() throws Exception {
            BookCreateRequest request = BookCreateRequest.builder()
                    .title("Clean Code")
                    .author("Robert C. Martin")
                    .isbn("978-0132350884")
                    .publishedDate(LocalDate.of(2008, 8, 1))
                    .description("A handbook of agile software craftsmanship")
                    .build();

            mockMvc.perform(post("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title", is("Clean Code")))
                    .andExpect(jsonPath("$.author", is("Robert C. Martin")))
                    .andExpect(jsonPath("$.isbn", is("978-0132350884")))
                    .andExpect(jsonPath("$.available", is(true)));
        }

        @Test
        @DisplayName("重复ISBN应返回409")
        void shouldReturn409ForDuplicateIsbn() throws Exception {
            BookCreateRequest request = BookCreateRequest.builder()
                    .title("Another Book")
                    .author("Another Author")
                    .isbn("978-0134685991") // same ISBN as existing
                    .build();

            mockMvc.perform(post("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", is("Book with ISBN '978-0134685991' already exists")));
        }

        @Test
        @DisplayName("缺少必填字段应返回400")
        void shouldReturn400ForMissingFields() throws Exception {
            BookCreateRequest request = BookCreateRequest.builder()
                    .title("")  // blank
                    .author(null) // null
                    .isbn("123") // too short
                    .build();

            mockMvc.perform(post("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.fieldErrors").isArray());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/books/{id} - 根据ID获取书籍")
    class GetBookByIdApiTests {

        @Test
        @DisplayName("应成功返回书籍")
        void shouldReturnBook() throws Exception {
            mockMvc.perform(get("/api/v1/books/{id}", existingBook.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title", is("Effective Java")))
                    .andExpect(jsonPath("$.author", is("Joshua Bloch")));
        }

        @Test
        @DisplayName("ID不存在应返回404")
        void shouldReturn404ForNonExistentId() throws Exception {
            mockMvc.perform(get("/api/v1/books/{id}", 99999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/books/isbn/{isbn} - 根据ISBN获取书籍")
    class GetBookByIsbnApiTests {

        @Test
        @DisplayName("应成功返回书籍")
        void shouldReturnBookByIsbn() throws Exception {
            mockMvc.perform(get("/api/v1/books/isbn/{isbn}", "978-0134685991"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isbn", is("978-0134685991")));
        }

        @Test
        @DisplayName("ISBN不存在应返回404")
        void shouldReturn404ForNonExistentIsbn() throws Exception {
            mockMvc.perform(get("/api/v1/books/isbn/{isbn}", "000-0000000000"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/books - 获取所有书籍")
    class GetAllBooksApiTests {

        @Test
        @DisplayName("应返回所有书籍列表")
        void shouldReturnAllBooks() throws Exception {
            mockMvc.perform(get("/api/v1/books"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].title", is("Effective Java")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/books/search - 搜索书籍")
    class SearchBooksApiTests {

        @Test
        @DisplayName("应能按作者搜索")
        void shouldSearchByAuthor() throws Exception {
            mockMvc.perform(get("/api/v1/books/search")
                            .param("type", "author")
                            .param("keyword", "Joshua"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("应能按标题搜索")
        void shouldSearchByTitle() throws Exception {
            mockMvc.perform(get("/api/v1/books/search")
                            .param("type", "title")
                            .param("keyword", "Effective"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("应能按可用性搜索")
        void shouldSearchByAvailability() throws Exception {
            mockMvc.perform(get("/api/v1/books/search")
                            .param("type", "available")
                            .param("keyword", "available"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("不支持的搜索类型应返回所有书籍")
        void shouldReturnAllForUnsupportedType() throws Exception {
            mockMvc.perform(get("/api/v1/books/search")
                            .param("type", "unknown")
                            .param("keyword", "test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/books/{id} - 更新书籍")
    class UpdateBookApiTests {

        @Test
        @DisplayName("应成功更新书籍")
        void shouldUpdateBook() throws Exception {
            BookUpdateRequest request = BookUpdateRequest.builder()
                    .title("Effective Java (3rd Edition)")
                    .description("Updated description")
                    .build();

            mockMvc.perform(put("/api/v1/books/{id}", existingBook.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title", is("Effective Java (3rd Edition)")))
                    .andExpect(jsonPath("$.description", is("Updated description")));
        }

        @Test
        @DisplayName("更新不存在的书籍应返回404")
        void shouldReturn404ForNonExistentBook() throws Exception {
            BookUpdateRequest request = BookUpdateRequest.builder().title("New Title").build();

            mockMvc.perform(put("/api/v1/books/{id}", 99999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("更新ISBN为已存在的ISBN应返回409")
        void shouldReturn409ForDuplicateIsbnOnUpdate() throws Exception {
            // 先创建第二本书
            Book secondBook = Book.builder()
                    .title("Clean Code")
                    .author("Robert C. Martin")
                    .isbn("978-0132350884")
                    .available(true)
                    .build();
            bookRepository.save(secondBook);

            BookUpdateRequest request = BookUpdateRequest.builder()
                    .isbn("978-0132350884")
                    .build();

            mockMvc.perform(put("/api/v1/books/{id}", existingBook.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/books/{id} - 删除书籍")
    class DeleteBookApiTests {

        @Test
        @DisplayName("应成功删除书籍并返回204")
        void shouldDeleteBookAndReturn204() throws Exception {
            mockMvc.perform(delete("/api/v1/books/{id}", existingBook.getId()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("删除不存在的书籍应返回404")
        void shouldReturn404ForNonExistentBook() throws Exception {
            mockMvc.perform(delete("/api/v1/books/{id}", 99999L))
                    .andExpect(status().isNotFound());
        }
    }
}