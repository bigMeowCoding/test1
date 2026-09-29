package com.example.bookstore.infrastructure.persistence;

import com.example.bookstore.application.port.BookStore;
import com.example.bookstore.domain.book.Book;
import com.example.bookstore.repository.BookMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** 将 MyBatis 记录对象转换为业务层使用的领域模型。 */
@Component
public final class BookPersistenceAdapter implements BookStore {
    private final BookMapper mapper;

    public BookPersistenceAdapter(BookMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<Book> findByKeyword(String keyword, int offset, int limit) {
        return mapper.findByKeyword(keyword, offset, limit).stream().map(this::toDomain).toList();
    }

    @Override
    public long countByKeyword(String keyword) {
        return mapper.countByKeyword(keyword);
    }

    @Override
    public Optional<Book> findById(long id) {
        return mapper.findById(id).map(this::toDomain);
    }

    @Override
    public int insert(Book book) {
        return mapper.insert(toRecord(book));
    }

    @Override
    public int update(Book book) {
        return mapper.update(toRecord(book));
    }

    @Override
    public int deleteById(long id) {
        return mapper.deleteById(id);
    }

    private Book toDomain(BookRecord record) {
        return new Book(record.getId(), record.getTitle(), record.getAuthor(), record.getPrice(), record.getStock());
    }

    private BookRecord toRecord(Book book) {
        return new BookRecord(book.id(), book.title(), book.author(), book.price(), book.stock());
    }
}
