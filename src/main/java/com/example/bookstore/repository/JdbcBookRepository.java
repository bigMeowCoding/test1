package com.example.bookstore.repository;

import com.example.bookstore.model.Book;
import com.example.bookstore.util.Database;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** 持久层实现：只负责 SQL、JDBC 映射和资源关闭，不放业务校验。 */
@Repository
public final class JdbcBookRepository implements BookRepository {
    @Override
    public List<Book> findByKeyword(String keyword, int offset, int limit) throws SQLException {
        String sql = "SELECT id, title, author, price, stock FROM books "
                + "WHERE title LIKE ? OR author LIKE ? ORDER BY id DESC LIMIT ? OFFSET ?";
        String pattern = "%" + keyword + "%";
        List<Book> books = new ArrayList<>();
        try (var connection = Database.getConnection(); var statement = connection.prepareStatement(sql)) {
            statement.setString(1, pattern);
            statement.setString(2, pattern);
            statement.setInt(3, limit);
            statement.setInt(4, offset);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) books.add(toBook(resultSet));
            }
        }
        return books;
    }

    @Override
    public long countByKeyword(String keyword) throws SQLException {
        String sql = "SELECT COUNT(*) FROM books WHERE title LIKE ? OR author LIKE ?";
        String pattern = "%" + keyword + "%";
        try (var connection = Database.getConnection(); var statement = connection.prepareStatement(sql)) {
            statement.setString(1, pattern);
            statement.setString(2, pattern);
            try (var resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1);
            }
        }
    }

    @Override
    public Optional<Book> findById(long id) throws SQLException {
        String sql = "SELECT id, title, author, price, stock FROM books WHERE id = ?";
        try (var connection = Database.getConnection(); var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(toBook(resultSet)) : Optional.empty();
            }
        }
    }

    @Override
    public long save(Book book) throws SQLException {
        String sql = "INSERT INTO books(title, author, price, stock) VALUES (?, ?, ?, ?)";
        try (var connection = Database.getConnection();
             var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindForWrite(statement, book);
            statement.executeUpdate();
            try (var keys = statement.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
            throw new SQLException("新增书籍后没有返回主键");
        }
    }

    @Override
    public boolean update(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, price = ?, stock = ? WHERE id = ?";
        try (var connection = Database.getConnection(); var statement = connection.prepareStatement(sql)) {
            bindForWrite(statement, book);
            statement.setLong(5, book.getId());
            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean deleteById(long id) throws SQLException {
        try (var connection = Database.getConnection();
             var statement = connection.prepareStatement("DELETE FROM books WHERE id = ?")) {
            statement.setLong(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    private void bindForWrite(java.sql.PreparedStatement statement, Book book) throws SQLException {
        statement.setString(1, book.getTitle());
        statement.setString(2, book.getAuthor());
        statement.setBigDecimal(3, book.getPrice());
        statement.setInt(4, book.getStock());
    }

    private Book toBook(java.sql.ResultSet resultSet) throws SQLException {
        return new Book(resultSet.getLong("id"), resultSet.getString("title"), resultSet.getString("author"),
                resultSet.getBigDecimal("price"), resultSet.getInt("stock"));
    }
}
