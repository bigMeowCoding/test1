package com.example.bookstore.domain.order;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/** 订单聚合根，负责维护订单行、总额与状态机的一致性。 */
public final class Order {
    private final Long id;
    private OrderStatus status;
    private final List<OrderLine> lines;
    private final Money totalAmount;
    private final Instant createdAt;
    private Instant paidAt;
    private Instant cancelledAt;

    private Order(Long id, OrderStatus status, List<OrderLine> lines, Money totalAmount,
                  Instant createdAt, Instant paidAt, Instant cancelledAt) {
        this.id = id;
        this.status = Objects.requireNonNull(status, "订单状态不能为空");
        this.lines = List.copyOf(lines);
        if (this.lines.isEmpty()) throw new IllegalArgumentException("订单至少需要一项商品");
        this.totalAmount = Objects.requireNonNull(totalAmount, "订单总额不能为空");
        if (!this.lines.stream().map(OrderLine::lineAmount).reduce(Money.zero(), Money::add).equals(totalAmount)) {
            throw new IllegalArgumentException("订单总额不正确");
        }
        this.createdAt = Objects.requireNonNull(createdAt, "创建时间不能为空");
        this.paidAt = paidAt;
        this.cancelledAt = cancelledAt;
    }

    public static Order create(List<OrderLine> lines, Instant createdAt) {
        Money total = lines.stream().map(OrderLine::lineAmount).reduce(Money.zero(), Money::add);
        return new Order(null, OrderStatus.PENDING_PAYMENT, lines, total, createdAt, null, null);
    }

    public static Order restore(Long id, OrderStatus status, List<OrderLine> lines, Money totalAmount,
                                Instant createdAt, Instant paidAt, Instant cancelledAt) {
        return new Order(id, status, lines, totalAmount, createdAt, paidAt, cancelledAt);
    }

    public void pay(Instant paidAt) {
        requirePending("支付");
        this.status = OrderStatus.PAID;
        this.paidAt = Objects.requireNonNull(paidAt, "支付时间不能为空");
    }

    public void cancel(Instant cancelledAt) {
        requirePending("取消");
        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = Objects.requireNonNull(cancelledAt, "取消时间不能为空");
    }

    private void requirePending(String action) {
        if (status != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException(status == OrderStatus.PAID ? "订单已支付，不能" + action : "订单已取消，不能" + action);
        }
    }

    public Long id() { return id; }
    public OrderStatus status() { return status; }
    public List<OrderLine> lines() { return lines; }
    public Money totalAmount() { return totalAmount; }
    public Instant createdAt() { return createdAt; }
    public Instant paidAt() { return paidAt; }
    public Instant cancelledAt() { return cancelledAt; }
}
