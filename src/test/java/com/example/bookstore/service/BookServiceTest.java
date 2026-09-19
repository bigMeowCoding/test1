package com.example.bookstore.service;

import com.example.bookstore.model.Book;
import com.example.bookstore.repository.BookMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookServiceTest {
    @Test
    void createRejectsNegativeStockBeforeCallingRepository() {
        FakeBookMapper mapper = new FakeBookMapper();
        BookService service = new BookService(mapper);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.create("Java 入门", "张三", "59.90", "-1"));

        assertEquals("库存不能小于 0", exception.getMessage());
        assertEquals(0, mapper.savedBooks.size());
    }

    @Test
    void listLimitsBooksToOnePageAndNormalizesPageNumber() {
        FakeBookMapper mapper = new FakeBookMapper();
        for (int i = 1; i <= 6; i++) mapper.savedBooks.add(book(i));
        BookService service = new BookService(mapper);

        BookPage result = service.list("", 99);

        assertEquals(2, result.page());
        assertEquals(2, result.totalPages());
        assertEquals(1, result.books().size());
    }

    private Book book(long id) {
        return new Book(id, "书" + id, "作者", java.math.BigDecimal.TEN, 1);
    }

    /** 用内存替身隔离 MySQL，单元测试只验证 BookService 的业务规则。 */
    private static final class FakeBookMapper implements BookMapper {
        private final List<Book> savedBooks = new ArrayList<>();

        @Override public List<Book> findByKeyword(String keyword, int offset, int limit) {
            return savedBooks.stream().skip(offset).limit(limit).toList();
        }
        @Override public long countByKeyword(String keyword) { return savedBooks.size(); }
        @Override public Optional<Book> findById(long id) { return savedBooks.stream().filter(book -> book.getId() == id).findFirst(); }
        @Override public int insert(Book book) { savedBooks.add(book); return 1; }
        @Override public int update(Book book) { return 0; }
        @Override public int deleteById(long id) { return 0; }
    }
}
