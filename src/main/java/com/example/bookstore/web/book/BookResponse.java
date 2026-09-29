package com.example.bookstore.web.book;

import com.example.bookstore.domain.book.Book;

import java.math.BigDecimal;

/** 对外的书籍响应契约；领域模型字段变化不会自动成为 API 变化。 */
public record BookResponse(Long id, String title, String author, BigDecimal price, int stock) {
    public static BookResponse from(Book book) {
        return new BookResponse(book.id(), book.title(), book.author(), book.price(), book.stock());
    }
}
