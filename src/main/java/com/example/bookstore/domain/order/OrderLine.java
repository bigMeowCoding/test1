package com.example.bookstore.domain.order;

import java.util.Objects;

/** 订单行保存创建订单当时的图书名称及单价，不能随目录变更而改变。 */
public record OrderLine(long bookId, String bookTitle, Money unitPrice, int quantity, Money lineAmount) {
    public OrderLine {
        if (bookId <= 0) throw new IllegalArgumentException("书籍 ID 无效");
        if (bookTitle == null || bookTitle.isBlank()) throw new IllegalArgumentException("书名不能为空");
        Objects.requireNonNull(unitPrice, "单价不能为空");
        if (quantity <= 0) throw new IllegalArgumentException("数量必须大于 0");
        Objects.requireNonNull(lineAmount, "行金额不能为空");
        if (!unitPrice.multiply(quantity).equals(lineAmount)) {
            throw new IllegalArgumentException("订单行金额不正确");
        }
    }

    public static OrderLine snapshot(long bookId, String title, Money unitPrice, int quantity) {
        return new OrderLine(bookId, title, unitPrice, quantity, unitPrice.multiply(quantity));
    }
}
