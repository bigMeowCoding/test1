package com.example.bookstore.application.port;

import com.example.bookstore.domain.book.Book;

import java.util.List;
import java.util.Optional;

/**
 * 业务层使用的书籍存储端口；具体数据库和 MyBatis 实现位于基础设施层。
 */
public interface BookStore {
    List<Book> findByKeyword(String keyword, int offset, int limit);

    long countByKeyword(String keyword);

    Optional<Book> findById(long id);

    int insert(Book book);

    int update(Book book);

    int deleteById(long id);
}
