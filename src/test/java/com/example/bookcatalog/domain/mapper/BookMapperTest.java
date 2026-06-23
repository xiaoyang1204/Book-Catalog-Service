package com.example.bookcatalog.domain.mapper;

import com.example.bookcatalog.domain.dto.BookCreateRequest;
import com.example.bookcatalog.domain.dto.BookResponse;
import com.example.bookcatalog.domain.dto.BookUpdateRequest;
import com.example.bookcatalog.domain.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BookMapper 单元测试")
class BookMapperTest {

    private final LocalDate testDate = LocalDate.of(2018, 1, 6);

    @Nested
    @DisplayName("toEntity 测试")
    class ToEntityTests {

        @Test
        @DisplayName("应将BookCreateRequest转换为Book实体")
        void shouldConvertCreateRequestToEntity() {
            BookCreateRequest request = BookCreateRequest.builder()
                    .title("Effective Java")
                    .author("Joshua Bloch")
                    .isbn("978-0134685991")
                    .publishedDate(testDate)
                    .description("A must-read")
                    .build();

            Book book = BookMapper.toEntity(request);

            assertThat(book.getTitle()).isEqualTo("Effective Java");
            assertThat(book.getAuthor()).isEqualTo("Joshua Bloch");
            assertThat(book.getIsbn()).isEqualTo("978-0134685991");
            assertThat(book.getPublishedDate()).isEqualTo(testDate);
            assertThat(book.getDescription()).isEqualTo("A must-read");
            assertThat(book.isAvailable()).isTrue();
            assertThat(book.getId()).isNull();
        }
    }

    @Nested
    @DisplayName("mergeToEntity 测试")
    class MergeToEntityTests {

        private Book existingBook;

        @org.junit.jupiter.api.BeforeEach
        void setUp() {
            existingBook = Book.builder()
                    .id(1L)
                    .title("Effective Java")
                    .author("Joshua Bloch")
                    .isbn("978-0134685991")
                    .publishedDate(testDate)
                    .description("Original description")
                    .available(true)
                    .build();
        }

        @Test
        @DisplayName("应仅更新非空字段")
        void shouldMergeOnlyNonNullFields() {
            BookUpdateRequest request = BookUpdateRequest.builder()
                    .title("New Title")
                    .build();

            Book result = BookMapper.mergeToEntity(request, existingBook);

            assertThat(result.getTitle()).isEqualTo("New Title");
            assertThat(result.getAuthor()).isEqualTo("Joshua Bloch"); // unchanged
            assertThat(result.getIsbn()).isEqualTo("978-0134685991"); // unchanged
        }

        @Test
        @DisplayName("应能更新所有字段")
        void shouldMergeAllFields() {
            BookUpdateRequest request = BookUpdateRequest.builder()
                    .title("New Title")
                    .author("New Author")
                    .isbn("978-0132350884")
                    .publishedDate(LocalDate.of(2020, 6, 1))
                    .description("New description")
                    .available(false)
                    .build();

            Book result = BookMapper.mergeToEntity(request, existingBook);

            assertThat(result.getTitle()).isEqualTo("New Title");
            assertThat(result.getAuthor()).isEqualTo("New Author");
            assertThat(result.getIsbn()).isEqualTo("978-0132350884");
            assertThat(result.getPublishedDate()).isEqualTo(LocalDate.of(2020, 6, 1));
            assertThat(result.getDescription()).isEqualTo("New description");
            assertThat(result.isAvailable()).isFalse();
        }

        @Test
        @DisplayName("空请求不应修改任何字段")
        void shouldNotModifyAnyFieldForEmptyRequest() {
            BookUpdateRequest request = BookUpdateRequest.builder().build();

            Book result = BookMapper.mergeToEntity(request, existingBook);

            assertThat(result.getTitle()).isEqualTo("Effective Java");
            assertThat(result.getAuthor()).isEqualTo("Joshua Bloch");
            assertThat(result.isAvailable()).isTrue();
        }
    }

    @Nested
    @DisplayName("toResponse 测试")
    class ToResponseTests {

        @Test
        @DisplayName("应将Book实体转换为BookResponse")
        void shouldConvertEntityToResponse() {
            Book book = Book.builder()
                    .id(1L)
                    .title("Effective Java")
                    .author("Joshua Bloch")
                    .isbn("978-0134685991")
                    .publishedDate(testDate)
                    .description("A must-read")
                    .available(true)
                    .build();

            BookResponse response = BookMapper.toResponse(book);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getTitle()).isEqualTo("Effective Java");
            assertThat(response.getAuthor()).isEqualTo("Joshua Bloch");
            assertThat(response.getIsbn()).isEqualTo("978-0134685991");
            assertThat(response.getPublishedDate()).isEqualTo(testDate);
            assertThat(response.getDescription()).isEqualTo("A must-read");
            assertThat(response.isAvailable()).isTrue();
        }
    }
}
