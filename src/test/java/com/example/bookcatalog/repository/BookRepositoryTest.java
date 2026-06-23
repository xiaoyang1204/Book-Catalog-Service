package com.example.bookcatalog.repository;

import com.example.bookcatalog.domain.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("BookRepository 单元测试")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    private Book savedBook;

    @BeforeEach
    void setUp() {
        Book book = Book.builder()
                .title("Effective Java")
                .author("Joshua Bloch")
                .isbn("978-0134685991")
                .publishedDate(LocalDate.of(2018, 1, 6))
                .description("A must-read for Java developers")
                .available(true)
                .build();
        bookRepository.save(book);
        savedBook = book;
    }

    @Nested
    @DisplayName("findByIsbn 测试")
    class FindByIsbnTests {

        @Test
        @DisplayName("应能根据ISBN找到书籍")
        void shouldFindBookByIsbn() {
            Optional<Book> result = bookRepository.findByIsbn("978-0134685991");
            assertThat(result).isPresent();
            assertThat(result.get().getTitle()).isEqualTo("Effective Java");
        }

        @Test
        @DisplayName("ISBN不存在时应返回空")
        void shouldReturnEmptyForNonExistentIsbn() {
            Optional<Book> result = bookRepository.findByIsbn("000-0000000000");
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByIsbn 测试")
    class ExistsByIsbnTests {

        @Test
        @DisplayName("ISBN存在时应返回true")
        void shouldReturnTrueForExistingIsbn() {
            assertThat(bookRepository.existsByIsbn("978-0134685991")).isTrue();
        }

        @Test
        @DisplayName("ISBN不存在时应返回false")
        void shouldReturnFalseForNonExistentIsbn() {
            assertThat(bookRepository.existsByIsbn("000-0000000000")).isFalse();
        }
    }

    @Nested
    @DisplayName("findByAuthorContainingIgnoreCase 测试")
    class FindByAuthorTests {

        @Test
        @DisplayName("应能根据作者名模糊搜索（忽略大小写）")
        void shouldFindByAuthorIgnoreCase() {
            List<Book> result = bookRepository.findByAuthorContainingIgnoreCase("joshua");
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAuthor()).isEqualTo("Joshua Bloch");
        }

        @Test
        @DisplayName("作者名不匹配时应返回空列表")
        void shouldReturnEmptyForNonMatchingAuthor() {
            List<Book> result = bookRepository.findByAuthorContainingIgnoreCase("Martin");
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByTitleContainingIgnoreCase 测试")
    class FindByTitleTests {

        @Test
        @DisplayName("应能根据标题模糊搜索（忽略大小写）")
        void shouldFindByTitleIgnoreCase() {
            List<Book> result = bookRepository.findByTitleContainingIgnoreCase("effective");
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("findByAvailable 测试")
    class FindByAvailableTests {

        @Test
        @DisplayName("应能查询可用的书籍")
        void shouldFindAvailableBooks() {
            List<Book> result = bookRepository.findByAvailable(true);
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("应能查询不可用的书籍")
        void shouldFindUnavailableBooks() {
            List<Book> result = bookRepository.findByAvailable(false);
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("CRUD 基础操作测试")
    class CrudTests {

        @Test
        @DisplayName("应能保存书籍")
        void shouldSaveBook() {
            Book newBook = Book.builder()
                    .title("Clean Code")
                    .author("Robert C. Martin")
                    .isbn("978-0132350884")
                    .available(true)
                    .build();
            bookRepository.save(newBook);
            assertThat(newBook.getId()).isNotNull();
            assertThat(newBook.getTitle()).isEqualTo("Clean Code");
        }

        @Test
        @DisplayName("应能更新书籍")
        void shouldUpdateBook() {
            savedBook.setTitle("Effective Java (3rd Edition)");
            bookRepository.update(savedBook);
            Optional<Book> updated = bookRepository.findById(savedBook.getId());
            assertThat(updated).isPresent();
            assertThat(updated.get().getTitle()).isEqualTo("Effective Java (3rd Edition)");
        }

        @Test
        @DisplayName("应能删除书籍")
        void shouldDeleteBook() {
            bookRepository.deleteById(savedBook.getId());
            assertThat(bookRepository.findById(savedBook.getId())).isEmpty();
        }

        @Test
        @DisplayName("应能查询所有书籍")
        void shouldFindAllBooks() {
            List<Book> books = bookRepository.findAll();
            assertThat(books).hasSize(1);
        }
    }
}