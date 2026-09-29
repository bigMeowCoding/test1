package com.example.bookstore.web.order;

import com.example.bookstore.domain.order.Order;
import com.example.bookstore.domain.order.OrderLine;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(long id, String status, List<Item> items, BigDecimal totalAmount, Instant createdAt,
                            Instant paidAt, Instant cancelledAt) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(order.id(), order.status().name(), order.lines().stream().map(Item::from).toList(),
                order.totalAmount().amount(), order.createdAt(), order.paidAt(), order.cancelledAt());
    }
    public record Item(long bookId, String bookTitle, BigDecimal unitPrice, int quantity, BigDecimal lineAmount) {
        static Item from(OrderLine line) { return new Item(line.bookId(), line.bookTitle(), line.unitPrice().amount(), line.quantity(), line.lineAmount().amount()); }
    }
}
