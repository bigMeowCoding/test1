package com.example.bookstore.service.order;

import com.example.bookstore.application.port.BookStore;
import com.example.bookstore.application.port.InventoryStore;
import com.example.bookstore.application.port.OrderStore;
import com.example.bookstore.domain.book.Book;
import com.example.bookstore.domain.order.Order;
import com.example.bookstore.service.ConflictException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderServiceTest {
    @Test
    void createMergesDuplicateItemsAndUsesCurrentBookSnapshot() {
        FakeBooks books = new FakeBooks(new Book(1L, "Java 入门", "作者", new BigDecimal("59.90"), 10));
        FakeInventory inventory = new FakeInventory(true);
        CapturingOrders orders = new CapturingOrders();
        Order result = new OrderService(books, inventory, orders).create(List.of(new OrderService.OrderItem(1, 1), new OrderService.OrderItem(1, 2)));

        assertEquals(1, result.lines().size());
        assertEquals(3, result.lines().get(0).quantity());
        assertEquals(new BigDecimal("179.70"), result.totalAmount().amount());
        assertEquals(List.of("reserve:1:3"), inventory.operations);
    }

    @Test
    void insufficientStockPreventsOrderPersistence() {
        FakeInventory inventory = new FakeInventory(false);
        CapturingOrders orders = new CapturingOrders();
        OrderService service = new OrderService(new FakeBooks(new Book(1L, "Java 入门", "作者", BigDecimal.TEN, 1)), inventory, orders);

        assertThrows(ConflictException.class, () -> service.create(List.of(new OrderService.OrderItem(1, 2))));
        assertEquals(0, orders.saved);
    }

    private record FakeBooks(Book book) implements BookStore {
        @Override public List<Book> findByKeyword(String keyword, int offset, int limit) { return List.of(); }
        @Override public long countByKeyword(String keyword) { return 0; }
        @Override public Optional<Book> findById(long id) { return id == book.id() ? Optional.of(book) : Optional.empty(); }
        @Override public int insert(Book book) { return 0; }
        @Override public int update(Book book) { return 0; }
        @Override public int deleteById(long id) { return 0; }
    }
    private static final class FakeInventory implements InventoryStore {
        private final boolean reserveResult; private final java.util.ArrayList<String> operations = new java.util.ArrayList<>();
        FakeInventory(boolean reserveResult) { this.reserveResult = reserveResult; }
        @Override public boolean reserve(long id, int quantity) { operations.add("reserve:" + id + ":" + quantity); return reserveResult; }
        @Override public boolean confirmSale(long id, int quantity) { return true; }
        @Override public boolean release(long id, int quantity) { return true; }
    }
    private static final class CapturingOrders implements OrderStore {
        int saved;
        @Override public Order save(Order order) { saved++; return order; }
        @Override public Optional<Order> findById(long id) { return Optional.empty(); }
        @Override public boolean updateFromPending(Order order) { return true; }
    }
}
