package com.example.bookstore.service.order;

import com.example.bookstore.application.port.BookStore;
import com.example.bookstore.application.port.InventoryStore;
import com.example.bookstore.application.port.OrderStore;
import com.example.bookstore.domain.book.Book;
import com.example.bookstore.domain.order.Money;
import com.example.bookstore.domain.order.Order;
import com.example.bookstore.domain.order.OrderLine;
import com.example.bookstore.service.ConflictException;
import com.example.bookstore.service.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 编排订单、目录和库存；每个用例的状态改变由同一数据库事务保护。 */
@Service
public class OrderService {
    private final BookStore bookStore;
    private final InventoryStore inventoryStore;
    private final OrderStore orderStore;
    private final Clock clock;

    @Autowired
    public OrderService(BookStore bookStore, InventoryStore inventoryStore, OrderStore orderStore) {
        this(bookStore, inventoryStore, orderStore, Clock.systemUTC());
    }

    OrderService(BookStore bookStore, InventoryStore inventoryStore, OrderStore orderStore, Clock clock) {
        this.bookStore = bookStore;
        this.inventoryStore = inventoryStore;
        this.orderStore = orderStore;
        this.clock = clock;
    }

    @Transactional
    public Order create(List<OrderItem> requestedItems) {
        Map<Long, Integer> quantities = mergeAndValidate(requestedItems);
        Map<Long, Book> books = bookStore.findByIds(List.copyOf(quantities.keySet())).stream()
                .collect(java.util.stream.Collectors.toMap(Book::id, book -> book));
        List<OrderLine> lines = quantities.entrySet().stream().map(entry -> snapshot(books, entry.getKey(), entry.getValue())).toList();
        for (OrderLine line : lines) {
            if (!inventoryStore.reserve(line.bookId(), line.quantity())) {
                throw new ConflictException("《" + line.bookTitle() + "》库存不足");
            }
        }
        return orderStore.save(Order.create(lines, Instant.now(clock)));
    }

    public Order get(long id) {
        return orderStore.findById(id).orElseThrow(() -> new NotFoundException("订单不存在"));
    }

    public List<Order> pending() { return orderStore.findPending(); }

    @Transactional
    public Order pay(long id) {
        Order order = get(id);
        try { order.pay(Instant.now(clock)); }
        catch (IllegalStateException exception) { throw new ConflictException(exception.getMessage()); }
        if (!orderStore.updateFromPending(order)) throw new ConflictException("订单状态已变更，请刷新后重试");
        for (OrderLine line : order.lines()) {
            if (!inventoryStore.confirmSale(line.bookId(), line.quantity())) {
                throw new IllegalStateException("订单预占库存不一致");
            }
        }
        return order;
    }

    @Transactional
    public Order cancel(long id) {
        Order order = get(id);
        try { order.cancel(Instant.now(clock)); }
        catch (IllegalStateException exception) { throw new ConflictException(exception.getMessage()); }
        if (!orderStore.updateFromPending(order)) throw new ConflictException("订单状态已变更，请刷新后重试");
        for (OrderLine line : order.lines()) {
            if (!inventoryStore.release(line.bookId(), line.quantity())) {
                throw new IllegalStateException("订单预占库存不一致");
            }
        }
        return order;
    }

    private Map<Long, Integer> mergeAndValidate(List<OrderItem> items) {
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("订单至少需要一项商品");
        Map<Long, Integer> quantities = new LinkedHashMap<>();
        for (OrderItem item : items) {
            if (item == null || item.bookId() <= 0 || item.quantity() <= 0) throw new IllegalArgumentException("订单商品不合法");
            try {
                quantities.merge(item.bookId(), item.quantity(), Math::addExact);
            } catch (ArithmeticException exception) {
                throw new IllegalArgumentException("订单数量过大", exception);
            }
        }
        return quantities;
    }

    private OrderLine snapshot(Map<Long, Book> books, long bookId, int quantity) {
        Book book = java.util.Optional.ofNullable(books.get(bookId)).orElseThrow(() -> new NotFoundException("书籍不存在"));
        return OrderLine.snapshot(bookId, book.title(), new Money(book.price()), quantity);
    }

    public record OrderItem(long bookId, int quantity) { }
}
