package com.example.bookstore.application.port;

import com.example.bookstore.domain.order.Order;

import java.util.Optional;
import java.util.List;

public interface OrderStore {
    Order save(Order order);
    Optional<Order> findById(long id);
    default List<Order> findPending() { return List.of(); }
    boolean updateFromPending(Order order);
}
