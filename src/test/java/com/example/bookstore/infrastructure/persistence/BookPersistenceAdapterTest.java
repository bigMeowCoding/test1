package com.example.bookstore.infrastructure.persistence;

import com.example.bookstore.domain.book.Book;
import com.example.bookstore.repository.BookMapper;
import com.example.bookstore.repository.InventoryMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookPersistenceAdapterTest {
    @Test
    void findByKeywordMapsPersistenceRecordsToDomainBooks() {
        BookMapper mapper = mock(BookMapper.class);
        when(mapper.findByKeyword("Java", 0, 5)).thenReturn(List.of(
                new BookRecord(3L, "Java 入门", "张三", new BigDecimal("59.90"), 10)));

        List<Book> books = new BookPersistenceAdapter(mapper, mock(InventoryMapper.class)).findByKeyword("Java", 0, 5);

        assertEquals(1, books.size());
        assertEquals(3L, books.get(0).id());
        assertEquals("Java 入门", books.get(0).title());
    }

    @Test
    void insertMapsDomainBookToTheMyBatisRecord() {
        BookMapper mapper = mock(BookMapper.class);
        Book book = new Book(null, "Java 入门", "张三", new BigDecimal("59.90"), 10);

        new BookPersistenceAdapter(mapper, mock(InventoryMapper.class)).insert(book);

        ArgumentCaptor<BookRecord> record = ArgumentCaptor.forClass(BookRecord.class);
        verify(mapper).insert(record.capture());
        assertEquals("Java 入门", record.getValue().getTitle());
        assertEquals(new BigDecimal("59.90"), record.getValue().getPrice());
    }

    @Test
    void updateSynchronizesTheAvailableInventoryShownToCustomers() {
        BookMapper mapper = mock(BookMapper.class);
        InventoryMapper inventoryMapper = mock(InventoryMapper.class);
        Book book = new Book(9L, "爱我别走33334", "作者", new BigDecimal("59.90"), 1);
        when(mapper.update(any(BookRecord.class))).thenReturn(1);
        when(inventoryMapper.updateAvailable(9L, 1)).thenReturn(1);

        int updated = new BookPersistenceAdapter(mapper, inventoryMapper).update(book);

        assertEquals(1, updated);
        verify(inventoryMapper).updateAvailable(9L, 1);
    }
}
