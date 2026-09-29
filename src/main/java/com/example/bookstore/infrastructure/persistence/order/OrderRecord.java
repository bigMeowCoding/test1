package com.example.bookstore.infrastructure.persistence.order;

import com.example.bookstore.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public final class OrderRecord {
    private Long id;
    private final OrderStatus status;
    private final BigDecimal totalAmount;
    private final Instant createdAt;
    private final Instant paidAt;
    private final Instant cancelledAt;

    public OrderRecord(Long id, OrderStatus status, BigDecimal totalAmount, Instant createdAt, Instant paidAt, Instant cancelledAt) {
        this.id = id;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
        this.cancelledAt = cancelledAt;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getPaidAt() { return paidAt; }
    public Instant getCancelledAt() { return cancelledAt; }
}
