package com.example.bookstore.web.book;

import com.example.bookstore.service.BookPage;

import java.util.List;

/** 书籍列表 API 的响应契约。 */
public record BookPageResponse(List<BookResponse> items, int page, int totalPages, long total) {
    public static BookPageResponse from(BookPage page) {
        return new BookPageResponse(page.books().stream().map(BookResponse::from).toList(),
                page.page(), page.totalPages(), page.total());
    }
}
