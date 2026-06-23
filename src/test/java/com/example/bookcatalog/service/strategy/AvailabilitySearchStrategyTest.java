package com.example.bookcatalog.service.strategy;

import com.example.bookcatalog.domain.entity.Book;
import com.example.bookcatalog.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("AvailabilitySearchStrategy 单元测试")
class AvailabilitySearchStrategyTest {

    private final BookRepository bookRepository = mock(BookRepository.class);
    private final AvailabilitySearchStrategy strategy = new AvailabilitySearchStrategy(bookRepository);

    private final Book availableBook = Book.builder()
            .id(1L)
            .title("Effective Java")
            .author("Joshua Bloch")
            .isbn("978-0134685991")
            .publishedDate(LocalDate.of(2018, 1, 6))
            .available(true)
            .build();

    private final Book unavailableBook = Book.builder()
            .id(2L)
            .title("Clean Code")
            .author("Robert C. Martin")
            .isbn("978-0132350884")
            .available(false)
            .build();

    @Test
    @DisplayName("supports 应对 'available' 返回 true")
    void shouldSupportAvailableType() {
        assertThat(strategy.supports("available")).isTrue();
        assertThat(strategy.supports("AVAILABLE")).isTrue();
    }

    @Test
    @DisplayName("supports 应对 'unavailable' 返回 true")
    void shouldSupportUnavailableType() {
        assertThat(strategy.supports("unavailable")).isTrue();
        assertThat(strategy.supports("UNAVAILABLE")).isTrue();
    }

    @Test
    @DisplayName("supports 应对其他类型返回 false")
    void shouldNotSupportOtherTypes() {
        assertThat(strategy.supports("author")).isFalse();
        assertThat(strategy.supports("title")).isFalse();
        assertThat(strategy.supports("unknown")).isFalse();
    }

    @Test
    @DisplayName("search 关键字为 'available' 应查询可用书籍")
    void shouldSearchAvailableBooks() {
        when(bookRepository.findByAvailable(true)).thenReturn(Collections.singletonList(availableBook));

        List<Book> results = strategy.search("available");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).isAvailable()).isTrue();
    }

    @Test
    @DisplayName("search 关键字为 'unavailable' 应查询不可用书籍")
    void shouldSearchUnavailableBooks() {
        when(bookRepository.findByAvailable(false)).thenReturn(Collections.singletonList(unavailableBook));

        List<Book> results = strategy.search("unavailable");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).isAvailable()).isFalse();
    }

    @Test
    @DisplayName("search 非标准关键字应默认查询不可用书籍")
    void shouldDefaultToUnavailableForNonStandardKeyword() {
        when(bookRepository.findByAvailable(false)).thenReturn(Collections.emptyList());

        List<Book> results = strategy.search("something");

        assertThat(results).isEmpty();
    }
}
