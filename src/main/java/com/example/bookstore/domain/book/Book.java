package com.example.bookstore.domain.book;

import java.math.BigDecimal;

/**
 * 书籍领域模型：表达业务层关心的书籍状态，不携带 HTTP 或 MyBatis 的实现细节。
 */
public final class Book {
    private final Long id;
    private final String title;
    private final String author;
    private final BigDecimal price;
    private final int stock;

    public Book(Long id, String title, String author, BigDecimal price, int stock) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
    }

    public Long id() { return id; }
    public String title() { return title; }
    public String author() { return author; }
    public BigDecimal price() { return price; }
    public int stock() { return stock; }
}
