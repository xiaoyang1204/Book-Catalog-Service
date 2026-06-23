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

@DisplayName("AuthorSearchStrategy 单元测试")
class AuthorSearchStrategyTest {

    private final BookRepository bookRepository = mock(BookRepository.class);
    private final AuthorSearchStrategy strategy = new AuthorSearchStrategy(bookRepository);

    private final Book testBook = Book.builder()
            .id(1L)
            .title("Effective Java")
            .author("Joshua Bloch")
            .isbn("978-0134685991")
            .publishedDate(LocalDate.of(2018, 1, 6))
            .available(true)
            .build();

    @Test
    @DisplayName("supports 应对 'author' 返回 true")
    void shouldSupportAuthorType() {
        assertThat(strategy.supports("author")).isTrue();
        assertThat(strategy.supports("AUTHOR")).isTrue();
        assertThat(strategy.supports("Author")).isTrue();
    }

    @Test
    @DisplayName("supports 应对非 'author' 类型返回 false")
    void shouldNotSupportOtherTypes() {
        assertThat(strategy.supports("title")).isFalse();
        assertThat(strategy.supports("available")).isFalse();
        assertThat(strategy.supports("unknown")).isFalse();
    }

    @Test
    @DisplayName("search 应调用 repository 按作者模糊搜索")
    void shouldSearchByAuthor() {
        when(bookRepository.findByAuthorContainingIgnoreCase("Joshua")).thenReturn(Collections.singletonList(testBook));

        List<Book> results = strategy.search("Joshua");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getAuthor()).isEqualTo("Joshua Bloch");
    }

    @Test
    @DisplayName("search 无匹配时应返回空列表")
    void shouldReturnEmptyWhenNoMatch() {
        when(bookRepository.findByAuthorContainingIgnoreCase("NonExistent")).thenReturn(Collections.emptyList());

        List<Book> results = strategy.search("NonExistent");

        assertThat(results).isEmpty();
    }
}
