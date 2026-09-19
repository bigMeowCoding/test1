package com.example.bookstore.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 新增或更新书籍的 HTTP 请求契约。
 *
 * <p>Controller 上的 {@code @Valid} 会在调用业务层前执行这些格式校验；
 * {@code BookService} 仍保留业务规则，避免 HTTP 入口成为唯一防线。</p>
 */
public record BookRequest(
        @NotBlank(message = "书名不能为空")
        @Size(max = 100, message = "书名不能超过 100 个字符")
        String title,

        @NotBlank(message = "作者不能为空")
        @Size(max = 60, message = "作者不能超过 60 个字符")
        String author,

        @NotNull(message = "价格不能为空")
        @DecimalMin(value = "0.0", inclusive = true, message = "价格不能小于 0")
        BigDecimal price,

        @NotNull(message = "库存不能为空")
        @jakarta.validation.constraints.Min(value = 0, message = "库存不能小于 0")
        Integer stock
) { }
