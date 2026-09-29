package com.example.bookstore.infrastructure.persistence;

import java.math.BigDecimal;

/**
 * MyBatis 的书籍持久化对象，只描述 books 表映射和 SQL 参数。
 */
public final class BookRecord {
    private final Long id;
    private final String title;
    private final String author;
    private final BigDecimal price;
    private final int stock;

    public BookRecord(Long id, String title, String author, BigDecimal price, int stock) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
}
