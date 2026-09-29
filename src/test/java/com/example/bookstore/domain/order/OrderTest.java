package com.example.bookstore.domain.order;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {
    @Test
    void paysPendingOrderAndRejectsSecondStateTransition() {
        Order order = Order.create(List.of(OrderLine.snapshot(1, "Java 入门", new Money(new BigDecimal("59.90")), 2)), Instant.EPOCH);

        order.pay(Instant.EPOCH.plusSeconds(1));

        assertEquals(OrderStatus.PAID, order.status());
        assertEquals(new Money(new BigDecimal("119.80")), order.totalAmount());
        assertThrows(IllegalStateException.class, () -> order.cancel(Instant.EPOCH.plusSeconds(2)));
    }
}
