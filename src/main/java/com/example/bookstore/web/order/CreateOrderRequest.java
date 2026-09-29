package com.example.bookstore.web.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateOrderRequest(@NotEmpty(message = "订单至少需要一项商品") List<@Valid Item> items) {
    public record Item(@NotNull(message = "书籍 ID 不能为空") @Positive(message = "书籍 ID 必须大于 0") Long bookId,
                       @NotNull(message = "数量不能为空") @Positive(message = "数量必须大于 0") Integer quantity) { }
}
