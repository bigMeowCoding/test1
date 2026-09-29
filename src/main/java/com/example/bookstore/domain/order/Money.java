package com.example.bookstore.domain.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** 人民币金额值对象，所有金额在边界处统一为两位小数。 */
public record Money(BigDecimal amount) {
    public Money {
        Objects.requireNonNull(amount, "金额不能为空");
        amount = amount.setScale(2, RoundingMode.HALF_UP);
        if (amount.signum() < 0) throw new IllegalArgumentException("金额不能小于 0");
    }

    public Money multiply(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("数量必须大于 0");
        return new Money(amount.multiply(BigDecimal.valueOf(quantity)));
    }

    public Money add(Money other) {
        return new Money(amount.add(other.amount));
    }

    public static Money zero() { return new Money(BigDecimal.ZERO); }
}
