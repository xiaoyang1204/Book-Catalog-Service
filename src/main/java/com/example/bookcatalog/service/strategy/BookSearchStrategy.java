package com.example.bookcatalog.service.strategy;

import com.example.bookcatalog.domain.entity.Book;

import java.util.List;

/**
 * 图书搜索策略接口
 * 设计模式: Strategy Pattern
 * 不同的搜索条件使用不同的搜索策略，便于扩展新的搜索方式
 */
public interface BookSearchStrategy {

    /**
     * 判断是否支持该搜索类型
     */
    boolean supports(String searchType);

    /**
     * 执行搜索
     */
    List<Book> search(String keyword);
}
