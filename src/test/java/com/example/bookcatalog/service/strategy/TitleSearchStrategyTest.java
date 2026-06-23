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

@DisplayName("TitleSearchStrategy 单元测试")
class TitleSearchStrategyTest {

    private final BookRepository bookRepository = mock(BookRepository.class);
    private final TitleSearchStrategy strategy = new TitleSearchStrategy(bookRepository);

    private final Book testBook = Book.builder()
            .id(1L)
            .title("Effective Java")
            .author("Joshua Bloch")
            .isbn("978-0134685991")
            .publishedDate(LocalDate.of(2018, 1, 6))
            .available(true)
            .build();

    @Test
    @DisplayName("supports 应对 'title' 返回 true")
    void shouldSupportTitleType() {
        assertThat(strategy.supports("title")).isTrue();
        assertThat(strategy.supports("TITLE")).isTrue();
        assertThat(strategy.supports("Title")).isTrue();
    }

    @Test
    @DisplayName("supports 应对非 'title' 类型返回 false")
    void shouldNotSupportOtherTypes() {
        assertThat(strategy.supports("author")).isFalse();
        assertThat(strategy.supports("available")).isFalse();
        assertThat(strategy.supports("unknown")).isFalse();
    }

    @Test
    @DisplayName("search 应调用 repository 按标题模糊搜索")
    void shouldSearchByTitle() {
        when(bookRepository.findByTitleContainingIgnoreCase("Effective")).thenReturn(Collections.singletonList(testBook));

        List<Book> results = strategy.search("Effective");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Effective Java");
    }

    @Test
    @DisplayName("search 无匹配时应返回空列表")
    void shouldReturnEmptyWhenNoMatch() {
        when(bookRepository.findByTitleContainingIgnoreCase("NonExistent")).thenReturn(Collections.emptyList());

        List<Book> results = strategy.search("NonExistent");

        assertThat(results).isEmpty();
    }
}
