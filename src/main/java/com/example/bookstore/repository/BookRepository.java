package com.example.bookstore.repository;

import com.example.bookstore.model.Book;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** 持久层接口：业务层只依赖它，不依赖 JDBC 的具体写法。 */
public interface BookRepository {
    List<Book> findByKeyword(String keyword, int offset, int limit) throws SQLException;
    long countByKeyword(String keyword) throws SQLException;
    Optional<Book> findById(long id) throws SQLException;
    long save(Book book) throws SQLException;
    boolean update(Book book) throws SQLException;
    boolean deleteById(long id) throws SQLException;
}
