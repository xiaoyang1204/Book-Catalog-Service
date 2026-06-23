package com.example.bookcatalog.service;

import com.example.bookcatalog.domain.dto.BookCreateRequest;
import com.example.bookcatalog.domain.dto.BookResponse;
import com.example.bookcatalog.domain.dto.BookUpdateRequest;
import com.example.bookcatalog.domain.entity.Book;
import com.example.bookcatalog.domain.exception.BookNotFoundException;
import com.example.bookcatalog.domain.exception.DuplicateIsbnException;
import com.example.bookcatalog.repository.BookRepository;
import com.example.bookcatalog.service.strategy.AuthorSearchStrategy;
import com.example.bookcatalog.service.strategy.AvailabilitySearchStrategy;
import com.example.bookcatalog.service.strategy.BookSearchStrategy;
import com.example.bookcatalog.service.strategy.TitleSearchStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService 单元测试")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    private List<BookSearchStrategy> searchStrategies;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private BookCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        searchStrategies = new ArrayList<>(Arrays.asList(
                new AuthorSearchStrategy(bookRepository),
                new TitleSearchStrategy(bookRepository),
                new AvailabilitySearchStrategy(bookRepository)
        ));
        bookService = new BookService(bookRepository, searchStrategies);

        testBook = Book.builder()
                .id(1L)
                .title("Effective Java")
                .author("Joshua Bloch")
                .isbn("978-0134685991")
                .publishedDate(LocalDate.of(2018, 1, 6))
                .description("A must-read for Java developers")
                .available(true)
                .build();

        createRequest = BookCreateRequest.builder()
                .title("Effective Java")
                .author("Joshua Bloch")
                .isbn("978-0134685991")
                .publishedDate(LocalDate.of(2018, 1, 6))
                .description("A must-read for Java developers")
                .build();
    }

    @Nested
    @DisplayName("createBook 测试")
    class CreateBookTests {

        @Test
        @DisplayName("应成功创建书籍")
        void shouldCreateBook() {
            when(bookRepository.existsByIsbn("978-0134685991")).thenReturn(false);

            BookResponse response = bookService.createBook(createRequest);

            assertThat(response).isNotNull();
            assertThat(response.getTitle()).isEqualTo("Effective Java");
            assertThat(response.getIsbn()).isEqualTo("978-0134685991");
            verify(bookRepository).save(any(Book.class));
        }

        @Test
        @DisplayName("ISBN重复时应抛出DuplicateIsbnException")
        void shouldThrowDuplicateIsbnException() {
            when(bookRepository.existsByIsbn("978-0134685991")).thenReturn(true);

            assertThatThrownBy(() -> bookService.createBook(createRequest))
                    .isInstanceOf(DuplicateIsbnException.class)
                    .hasMessageContaining("978-0134685991");
        }
    }

    @Nested
    @DisplayName("getBookById 测试")
    class GetBookByIdTests {

        @Test
        @DisplayName("应能根据ID获取书籍")
        void shouldGetBookById() {
            when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

            BookResponse response = bookService.getBookById(1L);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getTitle()).isEqualTo("Effective Java");
        }

        @Test
        @DisplayName("ID不存在时应抛出BookNotFoundException")
        void shouldThrowBookNotFoundException() {
            when(bookRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookService.getBookById(999L))
                    .isInstanceOf(BookNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }

    @Nested
    @DisplayName("getBookByIsbn 测试")
    class GetBookByIsbnTests {

        @Test
        @DisplayName("应能根据ISBN获取书籍")
        void shouldGetBookByIsbn() {
            when(bookRepository.findByIsbn("978-0134685991")).thenReturn(Optional.of(testBook));

            BookResponse response = bookService.getBookByIsbn("978-0134685991");

            assertThat(response).isNotNull();
            assertThat(response.getIsbn()).isEqualTo("978-0134685991");
        }

        @Test
        @DisplayName("ISBN不存在时应抛出BookNotFoundException")
        void shouldThrowBookNotFoundException() {
            when(bookRepository.findByIsbn("000-0000000000")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookService.getBookByIsbn("000-0000000000"))
                    .isInstanceOf(BookNotFoundException.class)
                    .hasMessageContaining("000-0000000000");
        }
    }

    @Nested
    @DisplayName("getAllBooks 测试")
    class GetAllBooksTests {

        @Test
        @DisplayName("应能获取所有书籍")
        void shouldGetAllBooks() {
            when(bookRepository.findAll()).thenReturn(Collections.singletonList(testBook));

            List<BookResponse> responses = bookService.getAllBooks();

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getTitle()).isEqualTo("Effective Java");
        }

        @Test
        @DisplayName("没有书籍时应返回空列表")
        void shouldReturnEmptyList() {
            when(bookRepository.findAll()).thenReturn(Collections.emptyList());

            List<BookResponse> responses = bookService.getAllBooks();

            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("updateBook 测试")
    class UpdateBookTests {

        @Test
        @DisplayName("应成功更新书籍")
        void shouldUpdateBook() {
            BookUpdateRequest updateRequest = BookUpdateRequest.builder()
                    .title("Effective Java (Updated)")
                    .build();
            when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

            BookResponse response = bookService.updateBook(1L, updateRequest);

            assertThat(response).isNotNull();
            verify(bookRepository).update(any(Book.class));
        }

        @Test
        @DisplayName("更新ISBN为已存在的ISBN时应抛出DuplicateIsbnException")
        void shouldThrowDuplicateIsbnWhenUpdatingToExistingIsbn() {
            BookUpdateRequest updateRequest = BookUpdateRequest.builder()
                    .isbn("978-0132350884")
                    .build();
            when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
            when(bookRepository.existsByIsbn("978-0132350884")).thenReturn(true);

            assertThatThrownBy(() -> bookService.updateBook(1L, updateRequest))
                    .isInstanceOf(DuplicateIsbnException.class)
                    .hasMessageContaining("978-0132350884");
        }

        @Test
        @DisplayName("更新不存在的书籍时应抛出BookNotFoundException")
        void shouldThrowBookNotFoundExceptionWhenUpdatingNonExistentBook() {
            BookUpdateRequest updateRequest = BookUpdateRequest.builder().title("New Title").build();
            when(bookRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookService.updateBook(999L, updateRequest))
                    .isInstanceOf(BookNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteBook 测试")
    class DeleteBookTests {

        @Test
        @DisplayName("应成功删除书籍")
        void shouldDeleteBook() {
            when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

            bookService.deleteBook(1L);

            verify(bookRepository).deleteById(1L);
        }

        @Test
        @DisplayName("删除不存在的书籍时应抛出BookNotFoundException")
        void shouldThrowBookNotFoundExceptionWhenDeletingNonExistentBook() {
            when(bookRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookService.deleteBook(999L))
                    .isInstanceOf(BookNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("searchBooks 测试")
    class SearchBooksTests {

        @Test
        @DisplayName("应能按作者搜索")
        void shouldSearchByAuthor() {
            when(bookRepository.findByAuthorContainingIgnoreCase("Joshua")).thenReturn(Collections.singletonList(testBook));

            List<BookResponse> responses = bookService.searchBooks("author", "Joshua");

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getAuthor()).isEqualTo("Joshua Bloch");
        }

        @Test
        @DisplayName("应能按标题搜索")
        void shouldSearchByTitle() {
            when(bookRepository.findByTitleContainingIgnoreCase("Effective")).thenReturn(Collections.singletonList(testBook));

            List<BookResponse> responses = bookService.searchBooks("title", "Effective");

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getTitle()).isEqualTo("Effective Java");
        }

        @Test
        @DisplayName("应能按可用性搜索")
        void shouldSearchByAvailability() {
            when(bookRepository.findByAvailable(true)).thenReturn(Collections.singletonList(testBook));

            List<BookResponse> responses = bookService.searchBooks("available", "available");

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).isAvailable()).isTrue();
        }

        @Test
        @DisplayName("不支持的搜索类型应返回所有书籍")
        void shouldReturnAllBooksForUnsupportedSearchType() {
            when(bookRepository.findAll()).thenReturn(Collections.singletonList(testBook));

            List<BookResponse> responses = bookService.searchBooks("unknown", "keyword");

            assertThat(responses).hasSize(1);
        }

        @Test
        @DisplayName("搜索无匹配结果时应返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            when(bookRepository.findByAuthorContainingIgnoreCase("NonExistent")).thenReturn(Collections.emptyList());

            List<BookResponse> responses = bookService.searchBooks("author", "NonExistent");

            assertThat(responses).isEmpty();
        }
    }
}