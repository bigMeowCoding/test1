package com.example.bookstore.infrastructure.persistence.order;

import com.example.bookstore.application.port.OrderStore;
import com.example.bookstore.domain.order.Money;
import com.example.bookstore.domain.order.Order;
import com.example.bookstore.domain.order.OrderLine;
import com.example.bookstore.repository.OrderMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public final class OrderPersistenceAdapter implements OrderStore {
    private final OrderMapper mapper;

    public OrderPersistenceAdapter(OrderMapper mapper) { this.mapper = mapper; }

    @Override
    public Order save(Order order) {
        OrderRecord record = toRecord(order);
        mapper.insertOrder(record);
        if (record.getId() == null) throw new IllegalStateException("订单保存失败");
        for (OrderLine line : order.lines()) mapper.insertLine(toRecord(record.getId(), line));
        return Order.restore(record.getId(), order.status(), order.lines(), order.totalAmount(), order.createdAt(), order.paidAt(), order.cancelledAt());
    }

    @Override
    public Optional<Order> findById(long id) {
        return mapper.findOrderById(id).map(this::toDomain);
    }

    @Override public List<Order> findPending() { return mapper.findPendingOrders().stream().map(this::toDomain).toList(); }

    @Override public boolean updateFromPending(Order order) { return mapper.updateFromPending(toRecord(order)) == 1; }

    private OrderRecord toRecord(Order order) {
        return new OrderRecord(order.id(), order.status(), order.totalAmount().amount(), order.createdAt(), order.paidAt(), order.cancelledAt());
    }
    private OrderLineRecord toRecord(long orderId, OrderLine line) {
        return new OrderLineRecord(null, orderId, line.bookId(), line.bookTitle(), line.unitPrice().amount(), line.quantity(), line.lineAmount().amount());
    }
    private OrderLine toDomain(OrderLineRecord line) {
        return new OrderLine(line.bookId(), line.bookTitle(), new Money(line.unitPrice()), line.quantity(), new Money(line.lineAmount()));
    }
    private Order toDomain(OrderRecord order) {
        return Order.restore(order.getId(), order.getStatus(), mapper.findLinesByOrderId(order.getId()).stream().map(this::toDomain).toList(),
                new Money(order.getTotalAmount()), order.getCreatedAt(), order.getPaidAt(), order.getCancelledAt());
    }
}
