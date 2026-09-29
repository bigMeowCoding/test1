package com.example.bookstore.infrastructure.persistence;

import com.example.bookstore.application.port.BookStore;
import com.example.bookstore.domain.book.Book;
import com.example.bookstore.repository.BookMapper;
import com.example.bookstore.repository.InventoryMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** 将 MyBatis 记录对象转换为业务层使用的领域模型。 */
@Component
public class BookPersistenceAdapter implements BookStore {
    private final BookMapper mapper;
    private final InventoryMapper inventoryMapper;

    public BookPersistenceAdapter(BookMapper mapper, InventoryMapper inventoryMapper) {
        this.mapper = mapper;
        this.inventoryMapper = inventoryMapper;
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
    public List<Book> findByIds(List<Long> ids) {
        return mapper.findByIds(ids).stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public int insert(Book book) {
        BookRecord record = toRecord(book);
        int inserted = mapper.insert(record);
        if (inserted == 1 && record.getId() != null && inventoryMapper.create(record.getId(), record.getStock()) != 1) {
            throw new IllegalStateException("初始化图书库存失败");
        }
        return inserted;
    }

    @Override
    @Transactional
    public int update(Book book) {
        int updated = mapper.update(toRecord(book));
        if (updated == 1 && inventoryMapper.updateAvailable(book.id(), book.stock()) != 1) {
            throw new IllegalStateException("更新图书库存失败");
        }
        return updated;
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
